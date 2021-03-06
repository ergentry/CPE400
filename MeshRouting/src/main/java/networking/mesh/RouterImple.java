/**
 *
 */
package networking.mesh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections15.Transformer;
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

		public Builder setId(final int id) {
			this.id = id;
			return this;
		}

		public Builder setModel(final Model model) {
			this.model = model;
			return this;
		}

		int getId() {
			return this.id;
		}

		Model getModel() {
			return this.model;
		}
	}

	private static final int LEADER_NOTIFICATION_TYPE = 2;

	static final Logger LOGGER = LogManager.getLogger(RouterImple.class.getName());

	public static Builder newInstance() {
		return new Builder();
	}

	Map<Integer, Integer> cache = new HashMap<>();
	private final int id;
	private volatile boolean leader;
	private final BlockingQueue<Message> messageQueue;
	private final Model model;

	private final AtomicInteger nextNonce = new AtomicInteger();

	private volatile Thread thread;

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
			message.setMessageStateAndCurrentLocation(MessageState.DROPPED, toString());
			return false;
		}

		try {
			if (message.getDestination() == this) {
				if (message.getPriority() == 1) {
					processControlMessage(message);
				}
				message.setMessageStateAndCurrentLocation(MessageState.RECEIVED, toString());
				return true;
			}

			message.setMessageStateAndCurrentLocation(MessageState.ENQUEUED, toString());

			LOGGER.info("Router: " + getID() + " Routing message " + message.getID() + " " + message.getSource().getID()
					+ " -> " + message.getDestination().getID());

			if (message.getRouteTo() == this) {
				message.setRouteTo(message.getDestination());
			}

			// COOL ROUTING ALGORITHM
			final Transformer<Link, Double> weight = new Transformer<Link, Double>() {
				@Override
				public Double transform(final Link input) {
					return input.isRunning() ? 1 : Double.MAX_VALUE;
				}
			};

			final DijkstraShortestPath<Router, Link> alg = new DijkstraShortestPath<>(this.model, weight);
			final List<Link> path = alg.getPath(this, message.getRouteTo());
			if (path.isEmpty()) {
				message.setMessageState(MessageState.UNROUTABLE);
			} else {
				final Link nextLink = path.get(0);
				final Router nextRouter = this.model.getOpposite(this, nextLink);
				return routeMessage(nextRouter, nextLink, message);
			}
		} catch (IllegalArgumentException | NullPointerException e) {
			message.setMessageStateAndCurrentLocation(MessageState.DROPPED, toString());
		}
		return true;
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
		messageQueue.add(message);
		this.model.notifyModelChanged();

		if (this.model.getLeader() != null) {
			message.setRouteTo(this.model.getLeader());
		} else {
			message.setRouteTo(dest);
		}
		return true;
	}

	@Override
	public void setLeader(final boolean leader) {
		this.leader = leader;
		this.model.setLeader(this);
		// Send a message to all routers that are connected to this router
		if (leader) {
			final int nonce = nextNonce.incrementAndGet();
			final Collection<Router> neighbors = this.model.getNeighbors(this);
			for (final Router neighbor : neighbors) {
				final Message notification = createLeaderNotification(neighbor, nonce);
				notification.setRouteTo(neighbor);
				messageQueue.add(notification);
			}
		}
		model.notifyModelChanged();
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

	private Message createLeaderNotification(final Router neighbor, final int nonce) {
		final Message message = model.newControlMessage(this, neighbor);
		message.setPriority(1);

		message.setPayload(createNotificationPayload(nonce));
		return message;
	}

	private byte[] createNotificationPayload(final int nonce) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			output.write(LEADER_NOTIFICATION_TYPE);
			output.write(nonce);
			output.write(getID());
			return output.toByteArray();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	private void processControlMessage(final Message message) {
		if (message.getTTL() == 0) {
			return;
		}
		int leadRouter = 0;
		int messageNonce = 0;

		try (ByteArrayInputStream input = new ByteArrayInputStream(message.getPayload())) {
			final int type = input.read();
			if (type != LEADER_NOTIFICATION_TYPE) {
				return;
			}
			messageNonce = input.read();
			leadRouter = input.read();
		} catch (final IOException e) {

			e.printStackTrace();
			return;
		}

		if (cache.containsKey(leadRouter)) {
			final int nonche = cache.get(leadRouter);
			if (nonche == messageNonce) {
				return;
			}
		}
		cache.put(leadRouter, messageNonce);

		if (leader && leadRouter != this.getID()) {
			this.setLeader(false);
		}

		final Collection<Router> neighbors = this.model.getNeighbors(this);
		for (final Router neighbor : neighbors) {
			if (!neighbor.equals(message.getSource())) {
				final Message notification = model.newControlMessage(this, neighbor);
				notification.setTTL(message.getTTL() - 1);
				notification.setPayload(message.getPayload());
				notification.setPriority(1);
				notification.setRouteTo(neighbor);
				messageQueue.add(notification);
			}
		}
		model.notifyModelChanged();

	}

	boolean routeMessage(final Router nextRouter, final Link nextLink, final Message message) {
		final LinkDirection direction = this.id < nextRouter.getID() ? LinkDirection.Left_To_Right
				: LinkDirection.Right_To_Left;
		if (!nextLink.isRunning()) {
			message.setMessageState(MessageState.UNROUTABLE);
			electLeader();
		}

		if (nextLink.inUse(direction)) {
			message.setMessageState(MessageState.ENQUEUED);
			messageQueue.add(message);
			this.model.notifyModelChanged();
			return true;
		}

		// Don't decrement the first hop.
		if (!message.getSource().equals(this)) {
			message.setTTL(message.getTTL() - 1);
		}

		if (message.getTTL() == 0) {
			message.setMessageState(MessageState.EXPIRED);
			return true;
		}

		final boolean result = nextLink.transmitMessage(direction, message);
		if (!result) {
			message.setMessageState(MessageState.UNROUTABLE);
			electLeader();
		}
		return true;
	}

}
