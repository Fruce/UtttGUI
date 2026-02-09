package network;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import network.packetes.HandshakePacket;
import network.packetes.MovePacket;
import network.packetes.NewGameOfferPacket;
import network.packetes.TakebackAcceptPacket;
import network.packetes.TakebackDenyPacket;
import network.packetes.TakebackOfferPacket;
import network.packetes.NewGameAcceptPacket;
import network.packetes.NewGameDenyPacket;

/*
 PacketCodec

 Responsible for encoding packets into byte[]
 and decoding byte[] back into packet objects.
*/
public final class PacketCodec {

    // Packet type identifiers (1 byte)
    private static final byte HANDSHAKE        = 1;
    private static final byte MOVE             = 2;
    private static final byte NEW_GAME_OFFER   = 3;
    private static final byte NEW_GAME_ACCEPT  = 4;
    private static final byte NEW_GAME_DENY    = 5;
    
    private static final byte TAKEBACK_OFFER   = 6;
    private static final byte TAKEBACK_ACCEPT  = 7;
    private static final byte TAKEBACK_DENY    = 8;

    private PacketCodec() {} // utility class

    /* ================= ENCODE ================= */

    public static byte[] encode(Object packet) {

        /* ---------- HANDSHAKE ---------- */
        if (packet instanceof HandshakePacket h) {

            byte[] nameBytes =
                    h.getUsername().getBytes(StandardCharsets.UTF_8);

            ByteBuffer buf =
                    ByteBuffer.allocate(1 + 4 + nameBytes.length);

            buf.put(HANDSHAKE);
            buf.putInt(nameBytes.length);
            buf.put(nameBytes);

            return buf.array();
        }

        /* ---------- MOVE ---------- */
        if (packet instanceof MovePacket m) {

            ByteBuffer buf =
                    ByteBuffer.allocate(1 + 4 + 4);

            buf.put(MOVE);
            buf.putInt(m.big);
            buf.putInt(m.small);

            return buf.array();
        }

        /* ---------- NEW GAME OFFER ---------- */
        if (packet instanceof NewGameOfferPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(NEW_GAME_OFFER);
            return buf.array();
        }

        /* ---------- NEW GAME ACCEPT ---------- */
        if (packet instanceof NewGameAcceptPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(NEW_GAME_ACCEPT);
            return buf.array();
        }

        /* ---------- NEW GAME DENY ---------- */
        if (packet instanceof NewGameDenyPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(NEW_GAME_DENY);
            return buf.array();
        }
        
        /* ---------- TAKEBACK OFFER ---------- */
        if (packet instanceof TakebackOfferPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(TAKEBACK_OFFER);
            return buf.array();
        }

        /* ---------- TAKEBACK ACCEPT ---------- */
        if (packet instanceof TakebackAcceptPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(TAKEBACK_ACCEPT);
            return buf.array();
        }

        /* ---------- TAKEBACK DENY ---------- */
        if (packet instanceof TakebackDenyPacket) {

            ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put(TAKEBACK_DENY);
            return buf.array();
        }

        throw new IllegalArgumentException(
                "Unknown packet type: " + packet.getClass()
        );
    }

    /* ================= DECODE ================= */

    public static Object decode(byte[] data) {

        ByteBuffer buf = ByteBuffer.wrap(data);
        byte type = buf.get();

        switch (type) {

            case HANDSHAKE -> {
                int len = buf.getInt();
                byte[] nameBytes = new byte[len];
                buf.get(nameBytes);

                String username =
                        new String(nameBytes, StandardCharsets.UTF_8);

                return new HandshakePacket(username);
            }

            case MOVE -> {
                int big = buf.getInt();
                int small = buf.getInt();
                return new MovePacket(big, small);
            }

            case NEW_GAME_OFFER -> {
                return new NewGameOfferPacket();
            }

            case NEW_GAME_ACCEPT -> {
                return new NewGameAcceptPacket();
            }

            case NEW_GAME_DENY -> {
                return new NewGameDenyPacket();
            }
            
            case TAKEBACK_OFFER -> {
                return new TakebackOfferPacket();
            }

            case TAKEBACK_ACCEPT -> {
                return new TakebackAcceptPacket();
            }

            case TAKEBACK_DENY -> {
                return new TakebackDenyPacket();
            }
            
            

            default -> throw new IllegalArgumentException(
                    "Unknown packet id: " + type
            );
        }
    }
}
