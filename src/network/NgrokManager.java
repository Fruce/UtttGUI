package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class NgrokManager {

    private static Process ngrokProcess;

    /* ================= START ================= */

    public static synchronized void startTunnel(int port) {
	    stopTunnel(); // stops existing tunnel 
	
	    new Thread(() -> {
	        try {
	            Path ngrokPath = NativeTools.getNgrokPath();
	
	            if (!Files.exists(ngrokPath)) {
	                throw new RuntimeException(
	                    "ngrok not found at: " + ngrokPath.toAbsolutePath()
	                );
	            }
	
	            ngrokPath.toFile().setExecutable(true);
	
	            /* ========== ADD AUTH TOKEN HERE ========== */
	
	            String token = NgrokAuthManager.getToken();
	            if (token != null && !token.isBlank()) {
	
	                ProcessBuilder auth = new ProcessBuilder(
	                    ngrokPath.toString(),
	                    "config",
	                    "add-authtoken",
	                    token
	                );
	
	                auth.redirectErrorStream(true);
	
	                Process authProcess = auth.start();
	                authProcess.waitFor(); // IMPORTANT
	            }
	
	            /* ========== START TCP TUNNEL ========== */
	
	            ProcessBuilder pb = new ProcessBuilder(
	                ngrokPath.toString(),
	                "tcp",
	                String.valueOf(port)
	            );
	
	            pb.redirectErrorStream(true);
	            ngrokProcess = pb.start();
	
	            try (BufferedReader reader =
	                     new BufferedReader(
	                         new InputStreamReader(
	                             ngrokProcess.getInputStream()))) {
	
	                String line;
	                while ((line = reader.readLine()) != null) {
	                    System.out.println("[NGROK] " + line);
	                }
	            }
	
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }, "ngrok-thread").start();
	}

    /* ================= STOP ================= */

    public static synchronized void stopTunnel() {
        if (ngrokProcess != null) {
            ngrokProcess.destroy();
            ngrokProcess = null;
        }
    }

    /* ================= API ================= */

    public static void fetchPublicUrl(Consumer<String> onReady) {
        new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(500);

                    URL url = new URL("http://127.0.0.1:4040/api/tunnels");
                    HttpURLConnection conn =
                            (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    try (BufferedReader br =
                                 new BufferedReader(
                                     new InputStreamReader(
                                         conn.getInputStream()))) {

                        String json =
                            br.lines().collect(Collectors.joining());

                        String publicUrl = extractPublicUrl(json);
                        if (publicUrl != null) {
                            onReady.accept(publicUrl);
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "ngrok-api-thread").start();
    }

    private static String extractPublicUrl(String json) {
        int key = json.indexOf("\"public_url\":\"");
        if (key == -1) return null;

        int start = key + "\"public_url\":\"".length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end).replace("\\/", "/");
    }

    /* ================= SHUTDOWN ================= */

    static {
        Runtime.getRuntime().addShutdownHook(
            new Thread(NgrokManager::stopTunnel)
        );
    }
}
