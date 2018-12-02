/**
 *
 */
package networking.mesh;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author emily
 *
 */
public class RouterImple implements Router {

	public static class Builder {
		private int id;
		private Model model;

		Builder() {
			// empty
		}

		public RouterImple build() {
			return new RouterImple(this);
		}

		int getId() {
			return this.id;
		}

		Model getModel() {
			return this.model;
		}

		public Builder setId(final int id) {
			this.id = id;
			return this;
		}

		public Builder setModel(final Model model) {
			this.model = model;
			return this;
		}
	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final int id;

	private final Queue<Message> messageQueue;

	private final Model model;

	RouterImple(final Builder builder) {
		this.id = builder.getId();
		this.messageQueue = new ArrayBlockingQueue<Message>(100);
		this.model = builder.getModel();
	}

	@Override
	public int compareTo(final Router o) {
		return Integer.compare(this.id, o.getID());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RouterImple other = (RouterImple) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public Queue<Message> getQueue() {
		return messageQueue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public void routeMessage(Message message) {
		if (message.getDestination() == this) {
			message.setMessageState(MessageState.RECEIVED);
		}
		final Collection<Link> links = this.model.getIncidentEdges(this);
		for (final Link link : links) {
			final Collection<Router> routers = model.getIncidentVertices(link);
			for (final Router router : routers) {
				if (router == message.getDestination()) {
					final LinkDirection direction = this.id < router.getID() ? LinkDirection.Left_To_Right
							: LinkDirection.Right_To_Left;
					if (link.inUse(direction)) {
						message.setMessageState(MessageState.ENQUEUED);
						messageQueue.add(message);
						return;
					} else {
						message.setMessageState(MessageState.IN_TRANSIT);
						link.transmitMessage(direction, message);
						return;
					}
				}
			}
		}

		// COOL ROUTING ALGORITHM HERE
		message.setMessageState(MessageState.UNROUTABLE);
	}

	@Override
	public void sendMessage(Router dest, int length) {
		final Message message = model.newDataMessage(this, dest, length);
		message.setMessageState(MessageState.ENQUEUED);
		messageQueue.add(message);

	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
