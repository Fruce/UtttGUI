package network;

import java.net.URI;

public class JoinCodeUtil {

    private static final String DOMAIN = "tcp.in.ngrok.io";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /*
     * Encode:
     * tcp://0.tcp.in.ngrok.io:19284 → AACBXU (always 6 letters)
     */
    public static String encode(String tcpUrl) {
        URI uri = URI.create(tcpUrl);

        String host = uri.getHost();   // 0.tcp.in.ngrok.io
        int port = uri.getPort();      // 19284

        if (host == null || port == -1) {
            throw new IllegalArgumentException("Invalid ngrok TCP URL");
        }

        int node = Integer.parseInt(host.split("\\.")[0]);

        String nodePart = padLeft(toLetters(node), 2);
        String portPart = padLeft(toLetters(port), 4);

        return nodePart + portPart;
    }

    /*
     * Decode:
     * AACBXU → tcp://0.tcp.in.ngrok.io:19284
     */
    public static String decode(String code) {
        if (code == null || code.length() != 6) {
            return null;
        }

        try {
            String nodePart = code.substring(0, 2);
            String portPart = code.substring(2, 6);

            int node = fromLetters(nodePart);
            int port = fromLetters(portPart);

            return node + "." + DOMAIN + ":" + port;

        } catch (Exception e) {
            return null;
        }
    }

    /* ================= helpers ================= */

    private static String toLetters(int n) {
        if (n == 0) return "A";

        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(ALPHABET.charAt(n % 26));
            n /= 26;
        }
        return sb.reverse().toString();
    }

    private static int fromLetters(String s) {
        int n = 0;
        for (char c : s.toCharArray()) {
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException();
            }
            n = n * 26 + (c - 'A');
        }
        return n;
    }

    private static String padLeft(String s, int len) {
        return "A".repeat(Math.max(0, len - s.length())) + s;
    }
}
