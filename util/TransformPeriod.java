package account.util;

import java.util.List;

public class TransformPeriod {

    private TransformPeriod() {};

    public static String transformPeriod(String period) {

        List<String> months = List.of("January", "February","March","April",
                "May", "June", "July", "August", "September", "October", "November", "December");

        String month = months.get(Integer.parseInt(period.substring(0,2)) - 1);

        return month + period.substring(2);
    }

}
