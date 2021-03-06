/**
 * https://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 * Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 * Last updated: Tue Mar 20 08:57:53 EDT 2018.
 */
package networking.mesh.util;

import java.util.LinkedList;
/******************************************************************************
 *  Compilation:  javac RedBlackBST.java
 *  Execution:    java RedBlackBST < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   https://algs4.cs.princeton.edu/33balanced/tinyST.txt
 *
 *  A symbol table implemented using a left-leaning red-black BST.
 *  This is the 2-3 version.
 *
 *  Note: commented out assertions because DrJava now enables assertions
 *        by default.
 *
 *  % more tinyST.txt
 *  S E A R C H E X A M P L E
 *
 *  % java RedBlackBST < tinyST.txt
 *  A 8
 *  C 4
 *  E 12
 *  H 5
 *  L 11
 *  M 9
 *  P 10
 *  R 3
 *  S 0
 *  X 7
 *
 ******************************************************************************/
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * The {@code BST} class represents an ordered symbol table of generic key-value
 * pairs. It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 * <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods. It also
 * provides ordered methods for finding the <em>minimum</em>, <em>maximum</em>,
 * <em>floor</em>, and <em>ceiling</em>. It also provides a <em>keys</em> method
 * for iterating over all of the keys. A symbol table implements the
 * <em>associative array</em> abstraction: when associating a value with a key
 * that is already in the symbol table, the convention is to replace the old
 * value with the new value. Unlike {@link java.util.Map}, this class uses the
 * convention that values cannot be {@code null}—setting the value associated
 * with a key to {@code null} is equivalent to deleting the key from the symbol
 * table.
 * <p>
 * This implementation uses a left-leaning red-black BST. It requires that the
 * key type implements the {@code Comparable} interface and calls the
 * {@code compareTo()} and method to compare two keys. It does not call either
 * {@code equals()} or {@code hashCode()}. The <em>put</em>, <em>contains</em>,
 * <em>remove</em>, <em>minimum</em>, <em>maximum</em>, <em>ceiling</em>, and
 * <em>floor</em> operations each take logarithmic time in the worst case, if
 * the tree becomes unbalanced. The <em>size</em>, and <em>is-empty</em>
 * operations take constant time. Construction takes constant time.
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/33balanced">Section 3.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. For other
 * implementations of the same API, see {@link ST}, {@link BinarySearchST},
 * {@link SequentialSearchST}, {@link BST}, {@link SeparateChainingHashST},
 * {@link LinearProbingHashST}, and {@link AVLTreeST}.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */

public class RedBlackBST<Key extends Comparable<Key>, Value>
{

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	/**
	 * Unit tests the {@code RedBlackBST} data type.
	 *
	 * @param args the command-line arguments
	 */
	public static void main(String[] args)
	{
		final RedBlackBST<String, Integer> st = new RedBlackBST<String, Integer>();
		final String input = "S E A R C H E X A M P L E";
		final String[] split = input.split(" ");
		int offset = 0;
		for (final String part : split)
		{
			st.put(part, Integer.valueOf(offset++));
		}

		for (final String s : st.keys())
		{
			System.out.println(s + " " + st.get(s));
		}

	}

	private Node root; // root of the BST

	/**
	 * Initializes an empty symbol table.
	 */
	public RedBlackBST()
	{
	}

	/**
	 * Returns the smallest key in the symbol table greater than or equal to
	 * {@code key}.
	 *
	 * @param key the key
	 * @return the smallest key in the symbol table greater than or equal to
	 *         {@code key}
	 * @throws NoSuchElementException   if there is no such key
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public Key ceiling(Key key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("argument to ceiling() is null");
		}
		if (isEmpty())
		{
			throw new NoSuchElementException("calls ceiling() with empty symbol table");
		}
		final Node x = ceiling(this.root, key);
		if (x == null)
		{
			return null;
		}
		return x.key;
	}

	/**
	 * Does this symbol table contain the given key?
	 *
	 * @param key the key
	 * @return {@code true} if this symbol table contains {@code key} and
	 *         {@code false} otherwise
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public boolean contains(Key key)
	{
		return get(key) != null;
	}

	/**
	 * Removes the specified key and its associated value from this symbol table (if
	 * the key is in this symbol table).
	 *
	 * @param key the key
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public void delete(Key key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("argument to delete() is null");
		}
		if (!contains(key))
		{
			return;
		}

		// if both children of root are black, set root to red
		if (!isRed(this.root.left) && !isRed(this.root.right))
		{
			this.root.color = RED;
		}

		this.root = delete(this.root, key);
		if (!isEmpty())
		{
			this.root.color = BLACK;
			assert check();
		}
	}

	/**
	 * Removes the largest key and associated value from the symbol table.
	 *
	 * @throws NoSuchElementException if the symbol table is empty
	 */
	public void deleteMax()
	{
		if (isEmpty())
		{
			throw new NoSuchElementException("BST underflow");
		}

		// if both children of root are black, set root to red
		if (!isRed(this.root.left) && !isRed(this.root.right))
		{
			this.root.color = RED;
		}

		this.root = deleteMax(this.root);
		if (!isEmpty())
		{
			this.root.color = BLACK;
			assert check();
		}
	}

	/***************************************************************************
	 * Standard BST search.
	 ***************************************************************************/

	/**
	 * Removes the smallest key and associated value from the symbol table.
	 *
	 * @throws NoSuchElementException if the symbol table is empty
	 */
	public void deleteMin()
	{
		if (isEmpty())
		{
			throw new NoSuchElementException("BST underflow");
		}

		// if both children of root are black, set root to red
		if (!isRed(this.root.left) && !isRed(this.root.right))
		{
			this.root.color = RED;
		}

		this.root = deleteMin(this.root);
		if (!isEmpty())
		{
			this.root.color = BLACK;
			assert check();
		}
	}

	/**
	 * Returns the largest key in the symbol table less than or equal to
	 * {@code key}.
	 *
	 * @param key the key
	 * @return the largest key in the symbol table less than or equal to {@code key}
	 * @throws NoSuchElementException   if there is no such key
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public Key floor(Key key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("argument to floor() is null");
		}
		if (isEmpty())
		{
			throw new NoSuchElementException("calls floor() with empty symbol table");
		}
		final Node x = floor(this.root, key);
		if (x == null)
		{
			return null;
		}
		return x.key;
	}

	/**
	 * Returns the value associated with the given key.
	 *
	 * @param key the key
	 * @return the value associated with the given key if the key is in the symbol
	 *         table and {@code null} if the key is not in the symbol table
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public Value get(Key key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("argument to get() is null");
		}
		return get(this.root, key);
	}

	/***************************************************************************
	 * Red-black tree insertion.
	 ***************************************************************************/

	/**
	 * Returns the height of the BST (for debugging).
	 *
	 * @return the height of the BST (a 1-node tree has height 0)
	 */
	public int height()
	{
		return height(this.root);
	}

	/**
	 * Is this symbol table empty?
	 *
	 * @return {@code true} if this symbol table is empty and {@code false}
	 *         otherwise
	 */
	public boolean isEmpty()
	{
		return this.root == null;
	}

	/***************************************************************************
	 * Red-black tree deletion.
	 ***************************************************************************/

	/**
	 * Returns all keys in the symbol table as an {@code Iterable}. To iterate over
	 * all of the keys in the symbol table named {@code st}, use the foreach
	 * notation: {@code for (Key key : st.keys())}.
	 *
	 * @return all keys in the symbol table as an {@code Iterable}
	 */
	public Iterable<Key> keys()
	{
		if (isEmpty())
		{
			return new LinkedList<Key>();
		}
		return keys(min(), max());
	}

	/**
	 * Returns all keys in the symbol table in the given range, as an
	 * {@code Iterable}.
	 *
	 * @param lo minimum endpoint
	 * @param hi maximum endpoint
	 * @return all keys in the sybol table between {@code lo} (inclusive) and
	 *         {@code hi} (inclusive) as an {@code Iterable}
	 * @throws IllegalArgumentException if either {@code lo} or {@code hi} is
	 *                                  {@code null}
	 */
	public Iterable<Key> keys(Key lo, Key hi)
	{
		if (lo == null)
		{
			throw new IllegalArgumentException("first argument to keys() is null");
		}
		if (hi == null)
		{
			throw new IllegalArgumentException("second argument to keys() is null");
		}

		final Queue<Key> queue = new LinkedList<Key>();
		// if (isEmpty() || lo.compareTo(hi) > 0) return queue;
		keys(this.root, queue, lo, hi);
		return queue;
	}

	/**
	 * Returns the largest key in the symbol table.
	 *
	 * @return the largest key in the symbol table
	 * @throws NoSuchElementException if the symbol table is empty
	 */
	public Key max()
	{
		if (isEmpty())
		{
			throw new NoSuchElementException("calls max() with empty symbol table");
		}
		return max(this.root).key;
	}

	/**
	 * Returns the smallest key in the symbol table.
	 *
	 * @return the smallest key in the symbol table
	 * @throws NoSuchElementException if the symbol table is empty
	 */
	public Key min()
	{
		if (isEmpty())
		{
			throw new NoSuchElementException("calls min() with empty symbol table");
		}
		return min(this.root).key;
	}

	/**
	 * Inserts the specified key-value pair into the symbol table, overwriting the
	 * old value with the new value if the symbol table already contains the
	 * specified key. Deletes the specified key (and its associated value) from this
	 * symbol table if the specified value is {@code null}.
	 *
	 * @param key the key
	 * @param val the value
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public void put(Key key, Value val)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("first argument to put() is null");
		}
		if (val == null)
		{
			delete(key);
			return;
		}

		this.root = put(this.root, key, val);
		this.root.color = BLACK;
		assert check();
	}

	/**
	 * Return the number of keys in the symbol table strictly less than {@code key}.
	 *
	 * @param key the key
	 * @return the number of keys in the symbol table strictly less than {@code key}
	 * @throws IllegalArgumentException if {@code key} is {@code null}
	 */
	public int rank(Key key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("argument to rank() is null");
		}
		return rank(key, this.root);
	}

	/**
	 * Return the key in the symbol table whose rank is {@code k}. This is the
	 * (k+1)st smallest key in the symbol table.
	 *
	 * @param k the order statistic
	 * @return the key in the symbol table of rank {@code k}
	 * @throws IllegalArgumentException unless {@code k} is between 0 and
	 *                                  <em>n</em>–1
	 */
	public Key select(int k)
	{
		if (k < 0 || k >= size())
		{
			throw new IllegalArgumentException("argument to select() is invalid: " + k);
		}
		final Node x = select(this.root, k);
		return x.key;
	}

	/**
	 * Returns the number of key-value pairs in this symbol table.
	 *
	 * @return the number of key-value pairs in this symbol table
	 */
	public int size()
	{
		return size(this.root);
	}

	/**
	 * Returns the number of keys in the symbol table in the given range.
	 *
	 * @param lo minimum endpoint
	 * @param hi maximum endpoint
	 * @return the number of keys in the sybol table between {@code lo} (inclusive)
	 *         and {@code hi} (inclusive)
	 * @throws IllegalArgumentException if either {@code lo} or {@code hi} is
	 *                                  {@code null}
	 */
	public int size(Key lo, Key hi)
	{
		if (lo == null)
		{
			throw new IllegalArgumentException("first argument to size() is null");
		}
		if (hi == null)
		{
			throw new IllegalArgumentException("second argument to size() is null");
		}

		if (lo.compareTo(hi) > 0)
		{
			return 0;
		}
		if (contains(hi))
		{
			return rank(hi) - rank(lo) + 1;
		}
		return rank(hi) - rank(lo);
	}

	// restore red-black tree invariant
	private Node balance(Node h)
	{
		assert h != null;
		Node tmp = h;

		if (isRed(tmp.right))
		{
			tmp = rotateLeft(tmp);
		}
		if (isRed(tmp.left) && isRed(tmp.left.left))
		{
			tmp = rotateRight(tmp);
		}
		if (isRed(tmp.left) && isRed(tmp.right))
		{
			flipColors(tmp);
		}

		tmp.size = size(tmp.left) + size(tmp.right) + 1;
		return tmp;
	}

	// the smallest key in the subtree rooted at x greater than or equal to the
	// given key
	private Node ceiling(Node x, Key key)
	{
		if (x == null)
		{
			return null;
		}
		final int cmp = key.compareTo(x.key);
		if (cmp == 0)
		{
			return x;
		}
		if (cmp > 0)
		{
			return ceiling(x.right, key);
		}
		final Node t = ceiling(x.left, key);
		if (t != null)
		{
			return t;
		}
		return x;
	}

	/***************************************************************************
	 * Check integrity of red-black tree data structure.
	 ***************************************************************************/
	private boolean check()
	{
		if (!isBST())
		{
			System.out.println("Not in symmetric order");
		}
		if (!isSizeConsistent())
		{
			System.out.println("Subtree counts not consistent");
		}
		if (!isRankConsistent())
		{
			System.out.println("Ranks not consistent");
		}
		if (!is23())
		{
			System.out.println("Not a 2-3 tree");
		}
		if (!isBalanced())
		{
			System.out.println("Not balanced");
		}
		return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
	}

	// delete the key-value pair with the given key rooted at h
	private Node delete(Node h, Key key)
	{
		assert get(h, key) != null;
		Node tmp = h;

		if (key.compareTo(tmp.key) < 0)
		{
			if (!isRed(tmp.left) && !isRed(tmp.left.left))
			{
				tmp = moveRedLeft(tmp);
			}
			tmp.left = delete(tmp.left, key);
		} else
		{
			if (isRed(tmp.left))
			{
				tmp = rotateRight(tmp);
			}
			if (key.compareTo(tmp.key) == 0 && tmp.right == null)
			{
				return null;
			}
			if (!isRed(tmp.right) && !isRed(tmp.right.left))
			{
				tmp = moveRedRight(tmp);
			}
			if (key.compareTo(tmp.key) == 0)
			{
				final Node x = min(tmp.right);
				tmp.key = x.key;
				tmp.val = x.val;
				// h.val = get(h.right, min(h.right).key);
				// h.key = min(h.right).key;
				tmp.right = deleteMin(tmp.right);
			} else
			{
				tmp.right = delete(tmp.right, key);
			}
		}
		return balance(tmp);
	}

	/***************************************************************************
	 * Utility functions.
	 ***************************************************************************/

	// delete the key-value pair with the maximum key rooted at h
	private Node deleteMax(Node h)
	{
		Node tmp = h;
		if (isRed(tmp.left))
		{
			tmp = rotateRight(tmp);
		}

		if (tmp.right == null)
		{
			return null;
		}

		if (!isRed(tmp.right) && !isRed(tmp.right.left))
		{
			tmp = moveRedRight(tmp);
		}

		tmp.right = deleteMax(tmp.right);

		return balance(tmp);
	}

	// delete the key-value pair with the minimum key rooted at h
	private Node deleteMin(Node h)
	{
		Node tmp = h;
		if (tmp.left == null)
		{
			return null;
		}

		if (!isRed(tmp.left) && !isRed(tmp.left.left))
		{
			tmp = moveRedLeft(tmp);
		}

		tmp.left = deleteMin(tmp.left);
		return balance(tmp);
	}

	/***************************************************************************
	 * Ordered symbol table methods.
	 ***************************************************************************/

	// flip the colors of a node and its two children
	private void flipColors(Node h)
	{
		// h must have opposite color of its two children
		assert h != null && h.left != null && h.right != null;
		assert !isRed(h) && isRed(h.left) && isRed(h.right) || isRed(h) && !isRed(h.left) && !isRed(h.right);
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// the largest key in the subtree rooted at x less than or equal to the given
	// key
	private Node floor(Node x, Key key)
	{
		if (x == null)
		{
			return null;
		}
		final int cmp = key.compareTo(x.key);
		if (cmp == 0)
		{
			return x;
		}
		if (cmp < 0)
		{
			return floor(x.left, key);
		}
		final Node t = floor(x.right, key);
		if (t != null)
		{
			return t;
		}
		return x;
	}

	// value associated with the given key in subtree rooted at x; null if no such
	// key
	private Value get(Node x, Key key)
	{
		Node tmp = x;
		while (tmp != null)
		{
			final int cmp = key.compareTo(tmp.key);
			if (cmp < 0)
			{
				tmp = tmp.left;
			} else if (cmp > 0)
			{
				tmp = tmp.right;
			} else
			{
				return tmp.val;
			}
		}
		return null;
	}

	private int height(Node x)
	{
		if (x == null)
		{
			return -1;
		}
		return 1 + Math.max(height(x.left), height(x.right));
	}

	// Does the tree have no red right links, and at most one (left)
	// red links in a row on any path?
	private boolean is23()
	{
		return is23(this.root);
	}

	private boolean is23(Node x)
	{
		if (x == null)
		{
			return true;
		}
		if (isRed(x.right))
		{
			return false;
		}
		if (x != this.root && isRed(x) && isRed(x.left))
		{
			return false;
		}
		return is23(x.left) && is23(x.right);
	}

	// do all paths from root to leaf have same number of black edges?
	private boolean isBalanced()
	{
		int black = 0; // number of black links on path from root to min
		Node x = this.root;
		while (x != null)
		{
			if (!isRed(x))
			{
				black++;
			}
			x = x.left;
		}
		return isBalanced(this.root, black);
	}

	// does every path from the root to a leaf have the given number of black links?
	private boolean isBalanced(Node x, int black)
	{
		int tmp = black;
		if (x == null)
		{
			return tmp == 0;
		}
		if (!isRed(x))
		{
			tmp--;
		}
		return isBalanced(x.left, tmp) && isBalanced(x.right, tmp);
	}

	// does this binary tree satisfy symmetric order?
	// Note: this test also ensures that data structure is a binary tree since order
	// is strict
	private boolean isBST()
	{
		return isBST(this.root, null, null);
	}

	// is the tree rooted at x a BST with all keys strictly between min and max
	// (if min or max is null, treat as empty constraint)
	// Credit: Bob Dondero's elegant solution
	private boolean isBST(Node x, Key min, Key max)
	{
		if (x == null)
		{
			return true;
		}
		if (min != null && x.key.compareTo(min) <= 0)
		{
			return false;
		}
		if (max != null && x.key.compareTo(max) >= 0)
		{
			return false;
		}
		return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
	}

	// check that ranks are consistent
	private boolean isRankConsistent()
	{
		for (int i = 0; i < size(); i++)
		{
			if (i != rank(select(i)))
			{
				return false;
			}
		}
		for (final Key key : keys())
		{
			if (key.compareTo(select(rank(key))) != 0)
			{
				return false;
			}
		}
		return true;
	}

	/***************************************************************************
	 * Node helper methods.
	 ***************************************************************************/
	// is node x red; false if x is null ?
	private boolean isRed(Node x)
	{
		if (x == null)
		{
			return false;
		}
		return x.color == RED;
	}

	/***************************************************************************
	 * Range count and range search.
	 ***************************************************************************/

	// are the size fields correct?
	private boolean isSizeConsistent()
	{
		return isSizeConsistent(this.root);
	}

	private boolean isSizeConsistent(Node x)
	{
		if (x == null)
		{
			return true;
		}
		if (x.size != size(x.left) + size(x.right) + 1)
		{
			return false;
		}
		return isSizeConsistent(x.left) && isSizeConsistent(x.right);
	}

	// add the keys between lo and hi in the subtree rooted at x
	// to the queue
	private void keys(Node x, java.util.Queue<Key> queue, Key lo, Key hi)
	{
		if (x == null)
		{
			return;
		}
		final int cmplo = lo.compareTo(x.key);
		final int cmphi = hi.compareTo(x.key);
		if (cmplo < 0)
		{
			keys(x.left, queue, lo, hi);
		}
		if (cmplo <= 0 && cmphi >= 0)
		{
			queue.add(x.key);
		}
		if (cmphi > 0)
		{
			keys(x.right, queue, lo, hi);
		}
	}

	// the largest key in the subtree rooted at x; null if no such key
	private Node max(Node x)
	{
		assert x != null;
		if (x.right == null)
		{
			return x;
		}
		return max(x.right);
	}

	// the smallest key in subtree rooted at x; null if no such key
	private Node min(Node x)
	{
		assert x != null;
		if (x.left == null)
		{
			return x;
		}
		return min(x.left);
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private Node moveRedLeft(Node h)
	{
		assert h != null;
		assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);
		Node tmp = h;

		flipColors(tmp);
		if (isRed(tmp.right.left))
		{
			tmp.right = rotateRight(tmp.right);
			tmp = rotateLeft(tmp);
			flipColors(tmp);
		}
		return tmp;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private Node moveRedRight(Node h)
	{
		assert h != null;
		assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
		Node tmp = h;
		flipColors(tmp);
		if (isRed(tmp.left.left))
		{
			tmp = rotateRight(tmp);
			flipColors(tmp);
		}
		return tmp;
	}

	// insert the key-value pair in the subtree rooted at h
	private Node put(Node h, Key key, Value val)
	{
		Node tmp = h;
		if (tmp == null)
		{
			return new Node(key, val, RED, 1);
		}

		final int cmp = key.compareTo(tmp.key);
		if (cmp < 0)
		{
			tmp.left = put(tmp.left, key, val);
		} else if (cmp > 0)
		{
			tmp.right = put(tmp.right, key, val);
		} else
		{
			tmp.val = val;
		}

		// fix-up any right-leaning links
		if (isRed(tmp.right) && !isRed(tmp.left))
		{
			tmp = rotateLeft(tmp);
		}
		if (isRed(tmp.left) && isRed(tmp.left.left))
		{
			tmp = rotateRight(tmp);
		}
		if (isRed(tmp.left) && isRed(tmp.right))
		{
			flipColors(tmp);
		}
		tmp.size = size(tmp.left) + size(tmp.right) + 1;

		return tmp;
	}

	// number of keys less than key in the subtree rooted at x
	private int rank(Key key, Node x)
	{
		if (x == null)
		{
			return 0;
		}
		final int cmp = key.compareTo(x.key);
		if (cmp < 0)
		{
			return rank(key, x.left);
		} else if (cmp > 0)
		{
			return 1 + size(x.left) + rank(key, x.right);
		} else
		{
			return size(x.left);
		}
	}

	// make a right-leaning link lean to the left
	private Node rotateLeft(Node h)
	{
		assert h != null && isRed(h.right);
		final Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.size = h.size;
		h.size = size(h.left) + size(h.right) + 1;
		return x;
	}

	/***************************************************************************
	 * Red-black tree helper functions.
	 ***************************************************************************/

	// make a left-leaning link lean to the right
	private Node rotateRight(Node h)
	{
		assert h != null && isRed(h.left);
		final Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.size = h.size;
		h.size = size(h.left) + size(h.right) + 1;
		return x;
	}

	// the key of rank k in the subtree rooted at x
	private Node select(Node x, int k)
	{
		assert x != null;
		assert k >= 0 && k < size(x);
		final int t = size(x.left);
		if (t > k)
		{
			return select(x.left, k);
		} else if (t < k)
		{
			return select(x.right, k - t - 1);
		} else
		{
			return x;
		}
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(Node x)
	{
		if (x == null)
		{
			return 0;
		}
		return x.size;
	}

	// BST helper node data type
	private class Node
	{
		Key key; // key
		Value val; // associated data
		Node left; // links to left and right subtrees
		Node right;
		boolean color; // color of parent link
		int size; // subtree count

		public Node(Key key, Value val, boolean color, int size)
		{
			this.key = key;
			this.val = val;
			this.color = color;
			this.size = size;
		}
	}
}