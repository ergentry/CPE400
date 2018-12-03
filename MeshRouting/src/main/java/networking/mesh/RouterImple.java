/**
 *
 */
package networking.mesh;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author emily
 *
 */
public class RouterImple implements Router, Runnable {

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

	static final Logger LOGGER = LogManager.getLogger(RouterImple.class.getName());

	public static Builder newInstance() {
		return new Builder();
	}

	private final int id;

	private final BlockingQueue<Message> messageQueue;

	private final Model model;

	private volatile Thread thread;

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
	public boolean equals(final Object obj) {
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
	public void routeMessage(final Message message) {

		LOGGER.info("Router: " + getID() + " Routing message " + message.getID() + " " + message.getSource().getID()
				+ " -> " + message.getDestination().getID());

		if (message.getDestination() == this) {
			message.setMessageState(MessageState.RECEIVED);
			return;
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
						this.model.notifyModelChanged();
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
	public void run() {
		try {
			while (true) {

				final Message message = messageQueue.poll(500, TimeUnit.MILLISECONDS);
				if (message != null) {
					LOGGER.debug("Received message " + message.getID() + ".");
					this.model.notifyModelChanged();
					routeMessage(message);
				}
				Thread.sleep(500);
			}
		} catch (final InterruptedException e) {
			// empty for now ):
		}

	}

	@Override
	public void sendMessage(final Router dest, final int length) {
		final Message message = model.newDataMessage(this, dest, length);
		message.setMessageState(MessageState.ENQUEUED);
		messageQueue.add(message);
		this.model.notifyModelChanged();
	}

	@Override
	public void start() {
		if (thread != null && thread.isAlive()) {
			stop();
		}

		thread = new Thread(this);
		thread.start();

	}

	@Override
	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}

	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
