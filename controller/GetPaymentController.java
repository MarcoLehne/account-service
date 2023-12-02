package account.controller;

import account.DTO.PaymentResponse;
import account.DTO.Response;
import account.entity.AppUser;
import account.entity.LoginAttempt;
import account.entity.SalaryPayment;
import account.exception.GetPaymentException;
import account.exception.PaymentException;
import account.repository.LoginAttemptRepository;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import account.route.v1.PaymentRoute;
import account.util.TransformDollarCents;
import account.util.TransformPeriod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GetPaymentController {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    public GetPaymentController(UserRepository userRepository, PaymentRepository paymentRepository,LoginAttemptRepository loginAttemptRepository) {

        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @GetMapping(path = PaymentRoute.PATH)
    public ResponseEntity<?> getPayments(Authentication authentication,
                                                             @RequestParam(required = false) String period) {

        Optional<AppUser> optAppUser = userRepository.findUserByUsername(authentication.getName().toLowerCase());

        if (optAppUser.isEmpty()) {
            throw new PaymentException();
        }

        AppUser appUser = optAppUser.get();

        Optional<LoginAttempt> loginAttemptOpt = loginAttemptRepository.findById(appUser.getEmail().toLowerCase());

        if (loginAttemptOpt.isPresent()) {
            LoginAttempt loginAttempt = loginAttemptOpt.get();
            loginAttempt.resetFailedAttempts();
            loginAttemptRepository.save(loginAttempt);
        }

        if (period == null) {
            List<SalaryPayment> salaryPayments = paymentRepository.findByAppUser(appUser);
            List<PaymentResponse> paymentResponses = new ArrayList<>();

            populatePaymentResponses(salaryPayments, paymentResponses, appUser);

            return new ResponseEntity<>(paymentResponses, HttpStatus.OK);
        } else {
            Optional<SalaryPayment> optSalaryPayment = paymentRepository.findByPeriodAndAppUser(period, appUser);

            if (optSalaryPayment.isEmpty()) {
                throw new GetPaymentException();
            }

            SalaryPayment salaryPayment = optSalaryPayment.get();

            return new ResponseEntity<>(createPaymentResponse(salaryPayment, appUser), HttpStatus.OK);
        }
    }

    private void populatePaymentResponses(List<SalaryPayment> salaryPayments,
                                          List<PaymentResponse> paymentResponses,
                                          AppUser appUser) {
        for (SalaryPayment salaryPayment: salaryPayments) {
            paymentResponses.add(0, new PaymentResponse(
                    appUser.getName(),
                    appUser.getLastname(),
                    TransformPeriod.transformPeriod(salaryPayment.getPeriod()),
                    TransformDollarCents.transform(salaryPayment.getSalary())
            ));
        }
    }

    private PaymentResponse createPaymentResponse(SalaryPayment salaryPayment, AppUser appUser) {
        return new PaymentResponse(appUser.getName(),
                appUser.getLastname(),
                TransformPeriod.transformPeriod(salaryPayment.getPeriod()),
                TransformDollarCents.transform(salaryPayment.getSalary()));
    }
}
