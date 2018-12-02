package networking.mesh;

/**
 * Message state as a message tranisitions through the simulator.
 */
public enum MessageState
{
	/**
	 * The message is created but not yet on a node queue.
	 */
	CREATED(false),

	/**
	 * The message has been dropped. For example, the node or link is stopped.
	 */
	DROPPED(true),

	/**
	 * The message in on the queue of node.
	 */
	ENQUEUED(false),

	/**
	 * The message has expired.
	 */
	EXPIRED(true),

	/**
	 * The message is in transit on a link.
	 */
	IN_TRANSIT(false),

	/**
	 * The message was received by the destination node.
	 */
	RECEIVED(true),

	/**
	 * The message is not routable to the destination node from the current node.
	 */
	UNROUTABLE(true);

	private final boolean terminal;

	MessageState(final boolean terminal)
	{
		this.terminal = terminal;
	}

	public boolean isTerminal()
	{
		return this.terminal;
	}
}
