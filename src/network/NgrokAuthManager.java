package network;

import java.util.prefs.Preferences;

public final class NgrokAuthManager {

    private static final Preferences PREFS =
        Preferences.userNodeForPackage(NgrokAuthManager.class);

    private static final String KEY = "NGROK_AUTH_TOKEN";

    public static void saveToken(String token) {
        PREFS.put(KEY, token);
    }

    public static String getToken() {
        return PREFS.get(KEY, null);
    }

    public static boolean hasToken() {
        return getToken() != null && !getToken().isBlank();
    }
}
