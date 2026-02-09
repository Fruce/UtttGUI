package network;

public interface NetworkEndpoint {

    // Receive ANY packet type
    Object receive() throws InterruptedException;

    // Outbound
    void sendMove(int big, int small);
    
    void sendNewGameOffer();
    void sendNewGameAccept();
    void sendNewGameDeny();
    
    void sendTakebackOffer();
    void sendTakebackAccept();
    void sendTakebackDeny();

    void close();


}
