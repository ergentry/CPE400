/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public interface Link extends Comparable<Link> {

	/**
	 *
	 * @return Link ID
	 */
	int getID();

	Message getMessage(LinkDirection direction);

	/**
	 *
	 * @return is the link in use
	 */
	boolean inUse(LinkDirection direction);

	boolean isRunning();

	void sendMessage(LinkDirection direction, Message message);

	void setLeftRouter(Router left);

	void setRightRouter(Router right);

	void start();

	void stop();

	boolean transmitMessage(LinkDirection direction, Message message);
}
