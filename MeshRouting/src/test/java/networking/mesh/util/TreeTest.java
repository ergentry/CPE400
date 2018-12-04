package networking.mesh.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TreeTest {

	@Test
	public void testAddChild() {
		final Tree<Integer, String> tree = new Tree<>(5, "hello");
		tree.addChild(5, 7, "world");
		assertEquals("hello", tree.get(5));
		assertEquals("world", tree.get(7));
		assertEquals(null, tree.get(8));
	}

	@Test
	public void testConstructor() {
		final Tree<Integer, String> tree = new Tree<>(5, "hello");
		final String value = tree.get(5);
		assertEquals("hello", value);
	}

	@Test
	public void testContains() {
		final Tree<Integer, String> tree = new Tree<>(5, "hello");
		tree.addChild(5, 7, "world");
		tree.addChild(7, 6, "!");
		tree.addChild(5, 8, "!");
		assertTrue(tree.contains(5));
		assertTrue(tree.contains(6));
		assertTrue(tree.contains(7));
		assertTrue(tree.contains(8));
		assertFalse(tree.contains(9));
	}

	@Test
	public void testPath() {
		final Tree<Integer, String> tree = new Tree<>(5, "hello");
		tree.addChild(5, 7, "world");
		tree.addChild(7, 6, "!");
		tree.addChild(5, 8, "!");

		List<Integer> path = tree.path(0);
		assertEquals(0, path.size());

		path = tree.path(5);
		assertEquals(1, path.size());

		path = tree.path(7);
		assertEquals(2, path.size());
		assertEquals(Integer.valueOf(7), path.get(1));

		path = tree.path(6);
		assertEquals(3, path.size());
		assertEquals(Integer.valueOf(7), path.get(1));
		assertEquals(Integer.valueOf(6), path.get(2));
	}
}
