/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public class LinkImple implements Link
{

	public static Builder newInstance()
	{
		return new Builder();
	}

	private final int id;
	private final Model model;

	LinkImple(final Builder builder)
	{
		this.id = builder.getId();
		this.model = builder.getModel();
	}

	@Override
	public int compareTo(final Link o)
	{
		return Integer.compare(this.id, o.getID());
	}

	@Override
	public int getID()
	{
		return this.id;
	}

	public Model getModel()
	{
		return this.model;
	}

	@Override
	public boolean inUse()
	{
		return false;
	}

	public static class Builder
	{
		private int id;

		private Model model;

		Builder()
		{
			// empty
		}

		public LinkImple build()
		{
			return new LinkImple(this);
		}

		public Builder setId(final int id)
		{
			this.id = id;
			return this;
		}

		public Builder setModel(final Model model)
		{
			this.model = model;
			return this;
		}

		int getId()
		{
			return this.id;
		}

		Model getModel()
		{
			return this.model;
		}
	}

}
