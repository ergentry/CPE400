/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public interface Message {
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
	 * @param ttl - new time to live
	 */
	void setTTL(int ttl);
}
