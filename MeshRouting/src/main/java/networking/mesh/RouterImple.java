/**
 *
 */
package networking.mesh;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

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
	private volatile boolean leader;

	RouterImple(final Builder builder) {
		this.id = builder.getId();
		this.messageQueue = new ArrayBlockingQueue<Message>(100);
		this.model = builder.getModel();
		this.leader = false;
	}

	@Override
	public int compareTo(final Router o) {
		return Integer.compare(this.id, o.getID());
	}

	@Override
	public void electLeader() {
		// TODO Auto-generated method stub

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
	public boolean isLeader() {
		return this.leader;
	}

	@Override
	public boolean isRunning() {
		return this.thread != null && this.thread.isAlive();
	}

	@Override
	public void linkFailure(final Link link) {
		// update data model
		electLeader();
	}

	@Override
	public boolean routeMessage(final Message message) {

		if (!isRunning()) {
			message.setMessageState(MessageState.DROPPED);
			return false;
		}

		LOGGER.info("Router: " + getID() + " Routing message " + message.getID() + " " + message.getSource().getID()
				+ " -> " + message.getDestination().getID());

		try {
			if (message.getDestination() == this) {
				message.setMessageState(MessageState.RECEIVED);
				return true;
			}

			// LOOK at our neighbors
			final Collection<Link> links = this.model.getIncidentEdges(this);
			for (final Link link : links) {
				final Router router = model.getOpposite(this, link);
				if (router == message.getDestination()) {
					return routeMessage(router, link, message);
				}
			}

			// COOL ROUTING ALGORITHM
			final DijkstraShortestPath<Router, Link> alg = new DijkstraShortestPath<>(this.model);
			final List<Link> path = alg.getPath(this, message.getDestination());
			if (path.isEmpty()) {
				message.setMessageState(MessageState.UNROUTABLE);
				return false;
			} else {
				final Link nextLink = path.get(0);
				final Router nextRouter = this.model.getOpposite(this, nextLink);
				return routeMessage(nextRouter, nextLink, message);
			}
		} catch (IllegalArgumentException | NullPointerException e) {
			message.setMessageState(MessageState.DROPPED);
		}
		return true;
	}

	boolean routeMessage(final Router nextRouter, final Link nextLink, final Message message) {
		final LinkDirection direction = this.id < nextRouter.getID() ? LinkDirection.Left_To_Right
				: LinkDirection.Right_To_Left;
		if (nextLink.inUse(direction)) {
			message.setMessageState(MessageState.ENQUEUED);
			messageQueue.add(message);
			this.model.notifyModelChanged();
			return true;
		}

		message.setMessageState(MessageState.IN_TRANSIT);
		final boolean result = nextLink.transmitMessage(direction, message);
		if (!result) {
			message.setMessageState(MessageState.DROPPED);
			electLeader();
		}
		return result;
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
				Thread.sleep(100);
			}
		} catch (final InterruptedException e) {
			// empty for now ):
		}

	}

	@Override
	public boolean sendMessage(final Router dest, final int length) {
		if (!isRunning()) {
			return false;
		}
		final Message message = model.newDataMessage(this, dest, length);
		message.setMessageState(MessageState.ENQUEUED);
		messageQueue.add(message);
		this.model.notifyModelChanged();
		return true;
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
		return "R" + id;
	}

}
