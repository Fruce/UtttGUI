package utils;

import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class UserSettings {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(UserSettings.class);

    private static final String KEY_USERNAME = "username";
    private static final String DEFAULT_USERNAME = "Player";

    private static final StringProperty username =
            new SimpleStringProperty(
                PREFS.get(KEY_USERNAME, DEFAULT_USERNAME)
            );

    private UserSettings() {}

    public static StringProperty usernameProperty() {
        return username;
    }

    public static String getUsername() {
        return username.get();
    }

    public static void setUsername(String value) {
        if (value == null) return;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return;

        PREFS.put(KEY_USERNAME, trimmed);
        username.set(trimmed);   // THIS is what updates the UI
    }
}
