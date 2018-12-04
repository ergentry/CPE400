package networking.mesh;

import org.junit.Assert;
import org.junit.Test;

public class LinkImpleTest {

	public LinkImpleTest() {
		// empty
	}

	@Test
	public void testGetId() {
		final Link link = LinkImple.newInstance().setId(6).build();
		Assert.assertEquals(6, link.getID());

	}
}
