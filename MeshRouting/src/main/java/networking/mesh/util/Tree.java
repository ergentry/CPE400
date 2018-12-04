package networking.mesh.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tree<Key extends Comparable<Key>, Value> {

	// helper node data type
	private class Node {
		private final Key key;
		private final Node parent;
		private final Value val; // associated data

		private final List<Node> children;

		public Node(final Node parent, final Key key, final Value val) {
			this.parent = parent;
			this.key = key;
			this.val = val;
			this.children = new ArrayList<>();
		}

		synchronized boolean addChild(final Node node) {
			return this.children.add(node);
		}

		synchronized List<Node> getChildren() {
			return children;
		}

		Key getKey() {
			return key;
		}

		Node getParent() {
			return parent;
		}

		Value getVal() {
			return val;
		}
	}

	private final Node root;

	public Tree(final Key key, final Value value) {
		this.root = new Node(null, key, value);
	}

	public boolean addChild(final Key parent, final Key child, final Value value) {
		if (contains(child)) {
			return false;
		}

		final Node parentNode = find(this.root, parent);
		if (parentNode == null) {
			return false;
		}

		return parentNode.addChild(new Node(parentNode, child, value));
	}

	public boolean contains(final Key key) {
		return find(this.root, key) != null;
	}

	private Node find(final Node node, final Key key) {

		if (node.getKey().compareTo(key) == 0) {
			return node;
		}

		final List<Node> children = new ArrayList<>(node.getChildren());
		for (final Node child : children) {
			final Node result = find(child, key);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	public Value get(final Key key) {
		final Node node = find(this.root, key);
		return node != null ? node.getVal() : null;
	}

	/**
	 *
	 * @param key
	 * @return a path from the root to the key
	 */
	public List<Key> path(final Key key) {
		final List<Key> result = new ArrayList<>();
		Node node = find(this.root, key);
		if (node != null) {
			result.add(node.getKey());

			while (node.getParent() != null) {
				node = node.getParent();
				result.add(node.getKey());
			}
		}
		Collections.reverse(result);
		return result;
	}
}
