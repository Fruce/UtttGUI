package network;

public final class JoinCodeUtil {

    private static final String DOMAIN = ".trycloudflare.com";

    private JoinCodeUtil() {}

    // https://abc-def.trycloudflare.com -> abc-def
    public static String extractCode(String fullUrl) {
        if (fullUrl == null) return null;

        fullUrl =  fullUrl.replace("|", "").trim();

        fullUrl = fullUrl.replace("https://", "")
                         .replace("http://", "");
        
        System.out.println("FULL URL: "+fullUrl);
        if (!fullUrl.endsWith(DOMAIN)) return null;

        return fullUrl.substring(0, fullUrl.length() - DOMAIN.length());
    }

    /*
     * WebSocket version for client connections
     */
    public static String buildWebSocketUrl(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        if (!code.matches("[a-z0-9-]+")) {
            return null;
        }

        return "wss://" + code + DOMAIN;
    }
}
