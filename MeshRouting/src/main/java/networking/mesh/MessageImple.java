/**
 *
 */
package networking.mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author emily
 *
 */
public class MessageImple implements Message {

	public static class Builder {
		private Router destination;
		private int id;
		private int length;
		private byte[] payload;
		private int priority;
		private Router source;
		private int ttl;

		Builder() {
			this.ttl = 15;
			this.priority = 0;
		}

		public MessageImple build() {
			return new MessageImple(this);
		}

		Router getDestination() {
			return this.destination;
		}

		int getId() {
			return this.id;
		}

		int getLength() {
			return this.length;
		}

		byte[] getPayload() {
			return this.payload;
		}

		int getPriority() {
			return this.priority;
		}

		Router getSource() {
			return this.source;
		}

		int getTtl() {
			return this.ttl;
		}

		public Builder setDestination(final Router destination) {
			this.destination = destination;
			return this;
		}

		public Builder setId(final int id) {
			this.id = id;
			return this;
		}

		public Builder setLength(final int length) {
			this.length = length;
			return this;
		}

		public Builder setPayload(final byte[] payload) {
			this.payload = payload;
			return this;
		}

		public Builder setPriority(final int priority) {
			this.priority = priority;
			return this;
		}

		public Builder setSource(final Router source) {
			this.source = source;
			return this;
		}

		public Builder setTtl(final int ttl) {
			this.ttl = ttl;
			return this;
		}

	}

	public static Builder newInstance() {
		return new Builder();
	}

	private final Router destination;
	private final int id;
	private volatile String currentLocation;
	private final int length;
	private final List<MessageListener> listeners;

	private volatile MessageState messageState;
	private final byte[] payload;
	private final int priority;
	private final Router source;
	private int TTL;

	MessageImple(final Builder builder) {
		this.id = builder.getId();
		this.length = builder.getLength();
		this.destination = builder.getDestination();
		this.payload = builder.getPayload();
		this.priority = builder.getPriority();
		this.source = builder.getSource();
		this.TTL = builder.getTtl();
		this.messageState = MessageState.CREATED;
		this.listeners = new ArrayList<>();
		this.currentLocation = this.source.toString();
	}

	@Override
	public void addMessageListener(final MessageListener messageListener) {
		this.listeners.add(messageListener);
	}

	@Override
	public int compareTo(final Message o) {
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
		final MessageImple other = (MessageImple) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String getCurrentLocation() {
		return currentLocation;
	}

	@Override
	public Router getDestination() {
		return this.destination;
	}

	@Override
	public int getID() {

		return this.id;
	}

	@Override
	public int getLength() {
		return this.length;
	}

	@Override
	public MessageState getMessageState() {
		return this.messageState;
	}

	@Override
	public byte[] getPayload() {

		return this.payload;
	}

	@Override
	public int getPriority() {

		return this.priority;
	}

	@Override
	public Router getSource() {

		return this.source;
	}

	@Override
	public int getTTL() {

		return this.TTL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	void notifyMessageListeners() {
		final List<MessageListener> receipients = new ArrayList<MessageListener>();
		receipients.addAll(listeners);
		receipients.forEach(l -> l.messageStateChanged(this));
	}

	@Override
	public void removeMessageListener(final MessageListener messageListener) {
		this.listeners.remove(messageListener);
	}

	@Override
	public void setCurrentLocation(final String currentLocation) {
		this.currentLocation = currentLocation;
		notifyMessageListeners();
	}

	@Override
	public void setMessageState(final MessageState state) {
		this.messageState = state;
		this.notifyMessageListeners();
	}

	@Override
	public void setMessageStateAndCurrentLocation(final MessageState state, final String location) {
		this.messageState = state;
		this.currentLocation = location;
		this.notifyMessageListeners();

	}

	@Override
	public void setTTL(final int ttl) {
		this.TTL = ttl;

	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
}