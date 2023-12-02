package account.DTO;

import account.route.v1.PaymentsRoute;

public class PaymentFailedResponse extends Response{

    public PaymentFailedResponse() {
        setMessage("Error!");
        setPath(PaymentsRoute.PATH);
    }
}
