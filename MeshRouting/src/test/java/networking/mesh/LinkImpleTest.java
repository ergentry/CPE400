package networking.mesh;

import org.junit.Assert;
import org.junit.Test;

public class LinkImpleTest {

	private final Model model;

	public LinkImpleTest() {
		this.model = new Model();
	}

	@Test
	public void testGetId() {
		final Link link = LinkImple.newInstance().setId(6).build();
		Assert.assertEquals(6, link.getID());

	}

	@Test
	public void testTransmitMessage() {
		final Router left = RouterImple.newInstance().setId(5).build();
		final Router right = RouterImple.newInstance().setId(7).build();
		final Link link = LinkImple.newInstance().setModel(model).setLeftRouter(left).setRightRouter(right).build();
		final Message message = model.newDataMessage(left, right, 2000);
		link.transmitMessage(LinkDirection.Left_To_Right, message);
		Assert.assertEquals(message, link.getMessage(LinkDirection.Left_To_Right));
		Assert.assertEquals(null, link.getMessage(LinkDirection.Right_To_Left));
	}

}
