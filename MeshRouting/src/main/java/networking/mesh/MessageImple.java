/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public class MessageImple implements Message {

	public static class Builder {
		private int ttl;
		private Router source;
		private Router destination;
		private int length;
		private byte[] payload;
		private int priority;
		private int id;

		Builder() {
			this.ttl = 15;
			this.priority = 0;
		}

		public MessageImple build() {
			return new MessageImple(this);
		}

		Router getDestination() {
			return destination;
		}

		int getId() {
			return id;
		}

		int getLength() {
			return length;
		}

		byte[] getPayload() {
			return payload;
		}

		int getPriority() {
			return priority;
		}

		Router getSource() {
			return source;
		}

		int getTtl() {
			return ttl;
		}

		public Builder setDestination(Router destination) {
			this.destination = destination;
			return this;
		}

		public Builder setId(int id) {
			this.id = id;
			return this;
		}

		public Builder setLength(int length) {
			this.length = length;
			return this;
		}

		public Builder setPayload(byte[] payload) {
			this.payload = payload;
			return this;
		}

		public Builder setPriority(int priority) {
			this.priority = priority;
			return this;
		}

		public Builder setSource(Router source) {
			this.source = source;
			return this;
		}

		public Builder setTtl(int ttl) {
			this.ttl = ttl;
			return this;
		}

	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final int id;
	private final int length;
	private final Router destination;
	private final byte[] payload;
	private final int priority;

	private final Router source;

	private int TTL;

	MessageImple(Builder builder) {
		this.id = builder.getId();
		this.length = builder.getLength();
		this.destination = builder.getDestination();
		this.payload = builder.getPayload();
		this.priority = builder.getPriority();
		this.source = builder.getSource();
		this.TTL = builder.getTtl();
	}

	@Override
	public Router getDestination() {
		return destination;
	}

	@Override
	public int getID() {

		return id;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public byte[] getPayload() {

		return payload;
	}

	@Override
	public int getPriority() {

		return priority;
	}

	@Override
	public Router getSource() {

		return source;
	}

	@Override
	public int getTTL() {

		return TTL;
	}

	@Override
	public void setTTL(int ttl) {
		this.TTL = ttl;

	}
}