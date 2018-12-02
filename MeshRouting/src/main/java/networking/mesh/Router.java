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
	 * @return the ID of the router
	 */
	int getID();

	Queue<Message> getQueue();

	void routeMessage(Message message);

	void sendMessage(Router dest, int length);
}
