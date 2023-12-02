package account.controller;

import account.DTO.AddingPaymentsSuccessfulResponse;
import account.DTO.PaymentTransaction;
import account.DTO.Response;
import account.route.v1.PaymentsRoute;
import account.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostPaymentController {

    private final PaymentService paymentService;

    public PostPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(path = PaymentsRoute.PATH)
    public ResponseEntity<?> processPayments(@RequestBody List<PaymentTransaction> payments) {

        paymentService.processPayments(payments);

        return new ResponseEntity<>(new AddingPaymentsSuccessfulResponse(), HttpStatus.OK);
    }

}
