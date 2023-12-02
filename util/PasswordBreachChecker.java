package account.util;

import account.security.BreachedPasswords;

public class PasswordBreachChecker {

    private PasswordBreachChecker() {};

    public static boolean isPasswordBreached(String password) {
        for (String breachedPassword: BreachedPasswords.breachedPasswords) {
            if (breachedPassword.equals(password)) {
                return true;
            }
        }
        return false;
    }

}
