package network.packetes;

import java.io.Serializable;

public class HandshakePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;

    public HandshakePacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}

