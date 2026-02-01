package network;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class NativeTools {

    private NativeTools() {}

    public static Path getAppDir() {
        try {
            return Paths.get(
                NativeTools.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            ).getParent();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot resolve app directory", e);
        }
    }

    public static Path getNgrokPath() {
        return getAppDir().resolve("tools/ngrok.exe");
    }
}
