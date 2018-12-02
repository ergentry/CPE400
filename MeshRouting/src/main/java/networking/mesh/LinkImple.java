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

		Builder() {
			// empty
		}

		public LinkImple build() {
			return new LinkImple(this);
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

	private final Model model;

	LinkImple(final Builder builder) {
		this.id = builder.getId();
		this.model = builder.getModel();
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

	@Override
	public int getID() {
		return this.id;
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean inUse() {
		return false;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
