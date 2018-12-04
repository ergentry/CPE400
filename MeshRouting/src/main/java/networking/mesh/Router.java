/**
 *
 */
package networking.mesh;

import java.util.Queue;

/**
 * @author emily
 *
 */
public interface Router extends Comparable<Router> {
	/**
	 *
	 */
	void electLeader();

	/**
	 *
	 * @return the ID of the router
	 */
	int getID();

	/**
	 *
	 * @return the queue of waiting messages. Used in UI and testing.
	 */
	Queue<Message> getQueue();

	/**
	 * @return is leader?
	 */
	boolean isLeader();

	/**
	 *
	 * @return is the router running?
	 */
	boolean isRunning();

	/**
	 * A linked has failed.
	 *
	 * @param link
	 */
	void linkFailure(Link link);

	/**
	 * route a message to the destination.
	 *
	 * @param message
	 */
	boolean routeMessage(Message message);

	/**
	 * Create a new data message annd send it to the dest router.
	 *
	 * @param dest
	 * @param length
	 */
	boolean sendMessage(Router dest, int length);

	/**
	 *
	 * @param b
	 */
	void setLeader(boolean b);

	/**
	 * Start executing the router.
	 */
	void start();

	/**
	 * Stop execution.
	 */
	void stop();
}
