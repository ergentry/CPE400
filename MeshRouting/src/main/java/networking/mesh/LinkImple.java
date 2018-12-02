/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public class LinkImple implements Link {

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

	public static Builder newInstance() {
		return new Builder();
	}

	private final Router leftRouter;

	private final Router rightRouter;

	private final int id;

	private final Model model;

	private volatile Message leftToRight;

	private volatile Message rightToLeft;

	LinkImple(final Builder builder) {
		this.id = builder.getId();
		this.model = builder.getModel();
		this.leftRouter = builder.getLeftRouter();
		this.rightRouter = builder.getRightRouter();
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
		// signal delay
		return true;
	}

}
