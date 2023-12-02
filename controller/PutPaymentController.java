package account.controller;

import account.DTO.PaymentTransaction;
import account.DTO.Response;
import account.DTO.UpdatingPaymentsSuccessfulResponse;
import account.route.v1.PaymentsRoute;
import account.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PutPaymentController {

    PaymentService paymentService;

    public PutPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping(path = PaymentsRoute.PATH)
    public ResponseEntity<?> putPayment(@RequestBody(required = false) PaymentTransaction paymentTransaction) {

        paymentService.updatePayments(paymentTransaction);
        return new ResponseEntity<>(new UpdatingPaymentsSuccessfulResponse(), HttpStatus.OK);
    }
}

