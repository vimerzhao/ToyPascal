package wci.message;

/**
 * MessageProducer
 *
 * All classes that produce messages must implement this interface.
 */
public interface MessageProducer {
    /**
     * Add a listener to the listener list.
     * @param listener the listener to addimpl.
     */
    public void addMessageListener(MessageListener listener);

    /**
     * Remove a listener from the listener list.
     * @param listener the listener to remove.
     */
    public void removeMessageListener(MessageListener listener);

    /**
     * Notify listeners after setting the message.
     * @param message the message to set.
     */
    public void sendMessage(Message message);
}
