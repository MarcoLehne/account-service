package account.DTO;

import account.route.v1.PaymentRoute;

public class GetPaymentFailedResponse extends Response{

    public GetPaymentFailedResponse() {
        setPath(PaymentRoute.PATH);
    }
}
