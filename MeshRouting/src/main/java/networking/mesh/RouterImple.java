/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public class RouterImple implements Router {

	public static class Builder {
		private int id;

		Builder() {
			// empty
		}

		public RouterImple build() {
			return new RouterImple(this);
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

	RouterImple(Builder builder) {
		this.id = builder.getId();
	}

	@Override
	public int getID() {
		return id;
	}

}
