package account.service;

import account.DTO.PaymentTransaction;
import account.entity.AppUser;
import account.entity.SalaryPayment;
import account.exception.PaymentException;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {

        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void processPayments(List<PaymentTransaction> payments) {

        for(PaymentTransaction paymentTransaction: payments) {

            AppUser appUser = secureGetUser(paymentTransaction.getEmployee());

            String period = paymentTransaction.getPeriod();
            Long salary = paymentTransaction.getSalary();

            if (! salaryAndPeriodOk(paymentTransaction)) {
                throw new PaymentException();
            }

            if (! periodUnique(period, appUser)) {
                throw new PaymentException();
            }

            paymentRepository.save(new SalaryPayment(period, salary, appUser));
        }
    }

    @Transactional
    public void updatePayments(PaymentTransaction paymentTransaction) {

        AppUser appUser = secureGetUser(paymentTransaction.getEmployee());
        Optional<SalaryPayment> optPayment = paymentRepository.findByPeriodAndAppUser(paymentTransaction.getPeriod(), appUser);

        if (optPayment.isEmpty()) {
            throw new PaymentException();
        }

        SalaryPayment payment = optPayment.get();

        payment.setSalary(paymentTransaction.getSalary());

        paymentRepository.save(payment);
    }

    private AppUser secureGetUser(String username) {
        Optional<AppUser> appUser = userRepository.findUserByUsername(username);

        if (appUser.isEmpty()) {
            throw new PaymentException();
        }

        return appUser.get();
    }

    private boolean salaryOk(Long salary) {
        return salary >= 0;
    }

    private boolean periodOk(String period) {
        return period.matches("^(0[1-9]|1[0-2])-(19[0-9]{2}|200[0-9]|201[0-9]|202[0-3])$");
    }


    private boolean salaryAndPeriodOk(PaymentTransaction paymentTransaction) {
        return salaryOk(paymentTransaction.getSalary()) && periodOk(paymentTransaction.getPeriod());
    }

    private boolean periodUnique(String period, AppUser appUser) {
        for (SalaryPayment salaryPayment: paymentRepository.findByAppUser(appUser)) {
            if (period.equals(salaryPayment.getPeriod())) {
                return false;
            }
        }
        return true;
    }

}
