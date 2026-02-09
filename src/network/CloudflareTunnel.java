package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class CloudflareTunnel {

    private static Process cloudflaredProcess;

    /* ================= START ================= */

    public static synchronized void startTunnel(
            int port,
            Consumer<String> onReady
    ) {
        stopTunnel(); // stop existing tunnel

        new Thread(() -> {
            try {
                Path cloudflaredPath = NativeTools.getCloudflaredPath();

                if (!Files.exists(cloudflaredPath)) {
                    throw new RuntimeException(
                        "cloudflared not found at: " +
                        cloudflaredPath.toAbsolutePath()
                    );
                }

                cloudflaredPath.toFile().setExecutable(true);

                ProcessBuilder pb = new ProcessBuilder(
                    cloudflaredPath.toString(),
                    "tunnel",
                    "--url",
                    "http://localhost:" + port
                );

                pb.redirectErrorStream(true);
                cloudflaredProcess = pb.start();

                try (BufferedReader reader =
                         new BufferedReader(
                             new InputStreamReader(
                                 cloudflaredProcess.getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[CLOUDFLARE] " + line);

                        String url = extractTryCloudflareUrl(line);
                        if (url != null) {
                            onReady.accept(url);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "cloudflared-thread").start();
    }

    /* ================= STOP ================= */

    public static synchronized void stopTunnel() {
        if (cloudflaredProcess != null) {
            cloudflaredProcess.destroy();
            cloudflaredProcess = null;
        }
    }

    /* ================= UTIL ================= */

    private static String extractTryCloudflareUrl(String line) {
        int idx = line.indexOf("https://");
        if (idx == -1) return null;

        String url = line.substring(idx).trim();

        if (url.contains("trycloudflare.com")) {
            return url;
        }
        return null;
    }

    /* ================= SHUTDOWN ================= */

    static {
        Runtime.getRuntime().addShutdownHook(
            new Thread(CloudflareTunnel::stopTunnel)
        );
    }
}
