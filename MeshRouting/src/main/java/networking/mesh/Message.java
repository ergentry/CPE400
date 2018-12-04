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
	 * @return the current location of the message
	 */
	String getCurrentLocation();

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

	/**
	 * set the current location of the message;
	 *
	 * @param location
	 */
	void setCurrentLocation(String location);

	/**
	 * set the message state of the message.
	 *
	 * @param state
	 */
	void setMessageState(MessageState state);

	void setMessageStateAndCurrentLocation(MessageState inTransit, String string);

	/**
	 *
	 * @param ttl - new time to live
	 */
	void setTTL(int ttl);
}
