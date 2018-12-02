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
	public void testSendMessage() {

		final Router source = RouterImple.newInstance().setModel(model).setId(1).build();
		final Router destination = RouterImple.newInstance().setId(2).build();
		source.sendMessage(destination, 1000);
		Assert.assertEquals(1, source.getQueue().size());
		final Message message = source.getQueue().poll();
		Assert.assertEquals(source, message.getSource());
		Assert.assertEquals(destination, message.getDestination());
		Assert.assertEquals(MessageState.ENQUEUED, message.getMessageState());
		Assert.assertEquals(1000, message.getLength());
	}
}
