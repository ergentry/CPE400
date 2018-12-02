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

		Builder() {
			// empty
		}

		public LinkImple build() {
			return new LinkImple(this);
		}

		int getId() {
			return id;
		}

		public Builder setId(int id) {
			this.id = id;
			return this;
		}
	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final int id;

	LinkImple(Builder builder) {
		this.id = builder.getId();
	}

	@Override
	public int getID() {
		return id;
	}

}
