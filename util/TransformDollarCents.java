package account.util;

public class TransformDollarCents {

    private TransformDollarCents() {}

    public static String transform(Long dollarCents) {

        String dollarCentsStr = String.valueOf(dollarCents);

        String dollarsStr = dollarCentsStr.substring(0, dollarCentsStr.length() - 2);
        if (dollarsStr.isBlank()) dollarsStr = "0";

        String centsStr = dollarCentsStr.substring(dollarCentsStr.length() - 2);
        if (centsStr.isBlank()) centsStr = "0";


        return dollarsStr + " dollar(s) " + centsStr + " cent(s)";
    }

}
