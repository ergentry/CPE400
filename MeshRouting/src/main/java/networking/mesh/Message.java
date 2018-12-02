/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public interface Message extends Comparable<Message> {
	/**
	 *
	 * @param messageListener
	 */
	void addMessageListener(MessageListener messageListener);

	/**
	 *
	 * @return Message destination
	 */
	Router getDestination();

	/**
	 *
	 * @return Message ID
	 */
	int getID();

	/**
	 *
	 * @return Message length
	 */
	int getLength();

	/**
	 *
	 * @return the message state
	 */
	MessageState getMessageState();

	/**
	 *
	 * @return Message payload
	 */
	byte[] getPayload();

	/**
	 *
	 * @return Message priority
	 */
	int getPriority();

	/**
	 *
	 * @return Message source
	 */
	Router getSource();

	/**
	 *
	 * @return Message time to live. 0 means message is expired
	 */
	int getTTL();

	/**
	 *
	 * @param messageListener
	 */
	void removeMessageListener(MessageListener messageListener);

	void setMessageState(MessageState state);

	/**
	 *
	 * @param ttl - new time to live
	 */
	void setTTL(int ttl);
}
