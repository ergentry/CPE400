package networking.mesh;

import org.junit.Assert;
import org.junit.Test;

public class RouterImpleTest {

	@Test
	public void testGetId() {
		final Router router = RouterImple.newInstance().setId(6).build();
		Assert.assertEquals(6, router.getID());

	}

}
