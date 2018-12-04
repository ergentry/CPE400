/**
 *
 */
package networking.mesh;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author emily
 *
 */
public class LinkImple implements Link, Runnable {

	public static class Builder {
		private int id;

		private Model model;

		private Router leftRouter;

		private Router rightRouter;

		Builder() {
			// empty
		}

		public LinkImple build() {
			return new LinkImple(this);
		}

		int getId() {
			return this.id;
		}

		Router getLeftRouter() {
			return leftRouter;
		}

		Model getModel() {
			return this.model;
		}

		Router getRightRouter() {
			return rightRouter;
		}

		public Builder setId(final int id) {
			this.id = id;
			return this;
		}

		public Builder setLeftRouter(final Router left) {
			this.leftRouter = left;
			return this;
		}

		public Builder setModel(final Model model) {
			this.model = model;
			return this;
		}

		public Builder setRightRouter(final Router right) {
			this.rightRouter = right;
			return this;
		}
	}

	private class LinkAction implements Runnable {

		Link link;
		LinkDirection direction;
		Message message;

		LinkAction(final Link link, final LinkDirection direction, final Message message) {
			this.link = link;
			this.direction = direction;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				final int time = message.getLength();
				LOGGER.info("Sleep " + time);
				Thread.sleep(time);
				link.sendMessage(direction, message);
			} catch (final InterruptedException e) {
				// empty, oh so empty
			}

		}

	}

	static final Logger LOGGER = LogManager.getLogger(LinkImple.class.getName());

	public static Builder newInstance() {
		return new Builder();
	}

	private volatile Thread thread;
	private volatile Router leftRouter;
	private volatile Router rightRouter;
	private final int id;
	private final Model model;
	private volatile Message leftToRight;
	private volatile Message rightToLeft;
	private final BlockingQueue<LinkAction> actionQueue;

	LinkImple(final Builder builder) {
		this.id = builder.getId();
		this.model = builder.getModel();
		this.leftRouter = builder.getLeftRouter();
		this.rightRouter = builder.getRightRouter();
		this.actionQueue = new ArrayBlockingQueue<LinkAction>(100);
	}

	@Override
	public int compareTo(final Link o) {
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
		final LinkImple other = (LinkImple) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public int getId() {
		return id;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public Router getLeftRouter() {
		return leftRouter;
	}

	public Message getLeftToRight() {
		return leftToRight;
	}

	@Override
	public Message getMessage(final LinkDirection direction) {

		return direction == LinkDirection.Left_To_Right ? leftToRight : rightToLeft;
	}

	public Model getModel() {
		return this.model;
	}

	public Router getRightRouter() {
		return rightRouter;
	}

	public Message getRightToLeft() {
		return rightToLeft;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public synchronized boolean inUse(final LinkDirection direction) {
		return direction == LinkDirection.Left_To_Right ? leftToRight != null : rightToLeft != null;
	}

	@Override
	public boolean isRunning() {
		return this.thread != null && this.thread.isAlive();
	}

	@Override
	public void run() {
		try {
			while (true) {
				LOGGER.debug("Running waiting for action");
				final LinkAction action = actionQueue.poll(500, TimeUnit.MILLISECONDS);
				if (action != null) {
					LOGGER.info("Executing action...");
					new Thread(action).start();
				}
			}
		} catch (final InterruptedException e) {
			// crying because empty
		}

	}

	@Override
	public void sendMessage(final LinkDirection direction, final Message message) {
		LOGGER.info("Sending message on its way " + direction + " " + message.getDestination().getID());
		try {
			final Pair<Router> routers = this.model.getEndpoints(this);

			if (routers == null || routers.getFirst() == null || routers.getSecond() == null) {
				stop();
			}

			final Router left = routers.getFirst().getID() < routers.getSecond().getID() ? routers.getFirst()
					: routers.getSecond();
			final Router right = routers.getFirst().getID() > routers.getSecond().getID() ? routers.getFirst()
					: routers.getSecond();
			boolean result = false;

			if (direction == LinkDirection.Left_To_Right) {
				LOGGER.info("Routing to " + right.getID());
				result = right.routeMessage(message);
				leftToRight = null;
			} else {
				LOGGER.info("Routing to " + left.getID());
				result = left.routeMessage(message);
				rightToLeft = null;
			}

			this.model.notifyModelChanged();

			if (!result) {
				stop();
			}

		} catch (final NullPointerException e) {
			stop();
		}
	}

	@Override
	public void setLeftRouter(final Router left) {
		this.leftRouter = left;
	}

	@Override
	public void setRightRouter(final Router right) {
		this.rightRouter = right;
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
		if (!isRunning()) {
			return;
		}

		if (thread != null) {
			thread.interrupt();
			thread = null;
		}

		if (this.leftToRight != null) {
			leftToRight.setMessageState(MessageState.DROPPED);
			leftToRight = null;
		}

		if (this.rightToLeft != null) {
			rightToLeft.setMessageState(MessageState.DROPPED);
			rightToLeft = null;
		}

		if (leftRouter != null) {
			leftRouter.linkFailure(this);
		}

		if (rightRouter != null) {
			rightRouter.linkFailure(this);
		}

	}

	@Override
	public String toString() {
		return "L" + id;
	}

	@Override
	public boolean transmitMessage(final LinkDirection direction, final Message message) {

		if (inUse(direction) || !isRunning()) {
			return false;
		}

		if (direction == LinkDirection.Left_To_Right) {
			leftToRight = message;
		} else {
			rightToLeft = message;
		}
		this.actionQueue.add(new LinkAction(this, direction, message));
		this.model.notifyModelChanged();

		return true;
	}

}
