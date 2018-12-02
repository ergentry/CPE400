package networking.mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class Model extends UndirectedSparseMultigraph<Router, Link> implements MessageFactory {
	static class LinkFactoryImpl implements LinkFactory {
		private static AtomicInteger nextSerialNumber = new AtomicInteger(0);
		private Model model;

		@Override
		public Link create() {
			final Link link = LinkImple.newInstance().setModel(this.model)
					.setId(LinkFactoryImpl.nextSerialNumber.incrementAndGet()).build();
			// link.start();
			return link;
		}

		public void setModel(final Model model) {
			this.model = model;
		}
	}

	static class MessageFactoryImpl implements MessageFactory {
		private static AtomicInteger nextSerialNumber = new AtomicInteger(0);
		private volatile MessageListener messageListener;
		private volatile int timeToLive;

		@Override
		public Message newControlMessage(final Router source, final Router destination) {
			final int id = MessageFactoryImpl.nextSerialNumber.getAndIncrement();
			final Message message = MessageImple.newInstance().setId(id).setSource(source).setDestination(destination)
					.setTtl(this.timeToLive).build();
			if (this.messageListener != null) {
				message.addMessageListener(this.messageListener);
			}
			return message;
		}

		@Override
		public Message newDataMessage(final Router source, final Router destination, final int size) {
			final int id = MessageFactoryImpl.nextSerialNumber.getAndIncrement();
			final Message message = MessageImple.newInstance().setId(id).setSource(source).setDestination(destination)
					.setTtl(this.timeToLive).setLength(size).build();
			if (this.messageListener != null) {
				message.addMessageListener(this.messageListener);
			}
			return message;
		}

		@Override
		public void setMessageListener(final MessageListener listener) {
			this.messageListener = listener;
		}

		@Override
		public void setTimeToLive(final int timeToLive) {
			this.timeToLive = timeToLive;
		}

	}

	static class RouterFactoryImpl implements RouterFactory {
		private static AtomicInteger nextSerialNumber = new AtomicInteger(0);
		private Model model;

		@Override
		public Router create() {
			final Router node = RouterImple.newInstance().setModel(this.model)
					.setId(RouterFactoryImpl.nextSerialNumber.getAndIncrement()).build();
			// node.start();
			return node;
		}

		public void setModel(final Model model) {
			this.model = model;
		}
	}

	private static final long serialVersionUID = 1L;
	private final LinkFactory linkFactory;
	private final List<ModelListener> listeners;

	private final MessageFactory messageFactory;

	private final TreeMap<Integer, Message> messages;

	private final RouterFactory nodeFactory;

	public Model() {
		this(new RouterFactoryImpl(), new LinkFactoryImpl(), new MessageFactoryImpl());
		((RouterFactoryImpl) this.nodeFactory).setModel(this);
		((LinkFactoryImpl) this.linkFactory).setModel(this);
	}

	Model(final RouterFactory nodeFactory, final LinkFactory linkFactory, final MessageFactory messageFactory) {
		this.nodeFactory = nodeFactory;
		this.linkFactory = linkFactory;
		this.messageFactory = messageFactory;
		this.messages = new TreeMap<>();
		this.listeners = new ArrayList<>();
	}

	@Override
	public boolean addEdge(final Link edge, final Pair<? extends Router> endpoints, final EdgeType edgeType) {
		final boolean modified = super.addEdge(edge, endpoints, edgeType);
		if (modified) {
			this.notifyModelChanged();
		}
		return modified;
	}

	@Override
	public boolean addEdge(final Link edge, final Router left, final Router right) {
		final boolean modified = super.addEdge(edge, left, right);
		if (modified) {
			this.notifyModelChanged();
		}
		if (left.getID() < right.getID()) {
			edge.setLeftRouter(left);
			edge.setRightRouter(right);
		} else {
			edge.setLeftRouter(right);
			edge.setRightRouter(left);
		}
		return modified;
	}

	public void addModelListener(final ModelListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public boolean addVertex(final Router vertex) {
		final boolean updated = super.addVertex(vertex);
		if (updated) {
			this.notifyModelChanged();
		}
		return updated;
	}

	public synchronized Link createLink() {
		final Link link = this.linkFactory.create();
		return link;
	}

	public synchronized Router createNode() {
		final Router node = this.nodeFactory.create();
		this.addVertex(node);
		return node;
	}

	public Factory<Link> getLinkFactory() {
		return this.linkFactory;
	}

	public List<Message> getMessages() {
		return new ArrayList<>(this.messages.values());
	}

	public Factory<Router> getNodeFactory() {
		return this.nodeFactory;
	}

	@Override
	public synchronized Message newControlMessage(final Router source, final Router destination) {
		this.pruneMessages();
		final Message message = this.messageFactory.newControlMessage(source, destination);
		this.messages.put(message.getID(), message);
		return message;
	}

	@Override
	public synchronized Message newDataMessage(final Router source, final Router destination, final int size) {
		this.pruneMessages();
		final Message message = this.messageFactory.newDataMessage(source, destination, size);
		this.messages.put(message.getID(), message);
		this.notifyModelChanged();
		return message;
	}

	void notifyModelChanged() {
		this.listeners.forEach(ModelListener::modelUpdated);
	}

	private void pruneMessages() {
		if (this.messages.size() > 200) {
			final int first = this.messages.firstKey();
			this.messages.remove(first);
		}
	}

	public void remove(final Message message) {
		this.messages.remove(message.getID());
	}

	@Override
	public boolean removeEdge(final Link edge) {
		final boolean removed = super.removeEdge(edge);
		if (removed) {
			this.notifyModelChanged();
		}
		return removed;
	}

	public void removeModelListener(final ModelListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public boolean removeVertex(final Router vertex) {
		final boolean removed = super.removeVertex(vertex);
		if (removed) {
			this.notifyModelChanged();
		}
		return removed;
	}

	@Override
	public void setMessageListener(final MessageListener listener) {
		this.messageFactory.setMessageListener(listener);
	}

	@Override
	public void setTimeToLive(final int ttl) {
		this.messageFactory.setTimeToLive(ttl);
	}
}
