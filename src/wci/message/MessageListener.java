package wci.message;

/**
 * MessageListener
 * <p>
 * All classes that listen to messages must implement this interface.
 */
public interface MessageListener {
    /**
     * Called to receive a message sent by a message producer.
     *
     * @param message the message that was sent.
     */
    public void messageReceived(Message message);
}
