/**
 *
 */
package networking.mesh;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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

		public Builder setLeftRouter(Router left) {
			this.leftRouter = left;
			return this;
		}

		public Builder setModel(final Model model) {
			this.model = model;
			return this;
		}

		public Builder setRightRouter(Router right) {
			this.rightRouter = right;
			return this;
		}
	}

	private class LinkAction implements Runnable {

		Link link;
		LinkDirection direction;
		Message message;

		LinkAction(Link link, LinkDirection direction, Message message) {
			this.link = link;
			this.direction = direction;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				final int time = message.getLength() / 500;
				Thread.sleep(time);
				link.sendMessage(direction, message);
			} catch (final InterruptedException e) {
				// empty, oh so empty
			}

		}

	}

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

	private final Queue<LinkAction> actionQueue;

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
	public Message getMessage(LinkDirection direction) {

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
	public synchronized boolean inUse(LinkDirection direction) {
		return direction == LinkDirection.Left_To_Right ? leftToRight != null : rightToLeft != null;
	}

	@Override
	public void run() {
		try {
			while (true) {
				final LinkAction action = actionQueue.peek();
				if (action != null) {
					actionQueue.poll();
					new Thread(action).start();
				}
				Thread.sleep(500);
			}
		} catch (final InterruptedException e) {
			// crying because empty
		}

	}

	@Override
	public void sendMessage(LinkDirection direction, Message message) {
		final Pair<Router> routers = this.model.getEndpoints(this);
		final Router left = routers.getFirst().getID() < routers.getSecond().getID() ? routers.getFirst()
				: routers.getSecond();
		final Router right = routers.getFirst().getID() > routers.getSecond().getID() ? routers.getFirst()
				: routers.getSecond();
		if (direction == LinkDirection.Left_To_Right) {
			right.routeMessage(message);
			leftToRight = null;
		} else {
			left.routeMessage(message);
			rightToLeft = null;
		}
	}

	@Override
	public void setLeftRouter(Router left) {
		this.leftRouter = left;
	}

	@Override
	public void setRightRouter(Router right) {
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
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}

	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	public boolean transmitMessage(LinkDirection direction, Message message) {

		if (inUse(direction)) {
			return false;
		}
		if (direction == LinkDirection.Left_To_Right) {
			leftToRight = message;
		} else {
			rightToLeft = message;
		}
		this.actionQueue.add(new LinkAction(this, direction, message));
		return true;
	}

}
