package networking.mesh;

import org.junit.Assert;
import org.junit.Test;

public class RouterImpleTest {

	private final Model model;

	public RouterImpleTest() {
		this.model = new Model();
	}

	@Test
	public void testGetId() {
		final Router router = RouterImple.newInstance().setId(6).build();
		Assert.assertEquals(6, router.getID());

	}

	@Test
	public void testRouteMessage() {
		final Router left = model.getRouterFactory().create();
		final Router right = model.getRouterFactory().create();
		final Link link = model.getLinkFactory().create();
		model.addEdge(link, left, right);
		final Message message = model.newDataMessage(left, right, 2000);
		left.routeMessage(message);
		Assert.assertEquals(message, link.getMessage(LinkDirection.Left_To_Right));

	}

}
