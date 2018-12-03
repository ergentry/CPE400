package networking.mesh;

import java.util.Queue;

import org.junit.Assert;
import org.junit.Test;

public class MessageImpleTest {

	private class LocalRouter implements Router {

		@Override
		public int compareTo(final Router o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getID() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Queue<Message> getQueue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void routeMessage(Message message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendMessage(Router dest, int length) {
			// TODO Auto-generated method stub

		}

		@Override
		public void start() {
			// TODO Auto-generated method stub

		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub

		}

	}

	@Test
	public void testDefaultPriority() {
		final Message message = MessageImple.newInstance().build();
		Assert.assertEquals(0, message.getPriority());
	}

	@Test
	public void testDefaultTTL() {
		final Message message = MessageImple.newInstance().build();
		Assert.assertEquals(15, message.getTTL());
	}

	@Test
	public void testGetDestination() {
		final Router router = new LocalRouter();
		final Message message = MessageImple.newInstance().setDestination(router).build();
		Assert.assertEquals(router, message.getDestination());
	}

	@Test
	public void testGetId() {
		final Message message = MessageImple.newInstance().setId(5).build();
		Assert.assertEquals(5, message.getID());
	}

	@Test
	public void testGetLength() {
		final Message message = MessageImple.newInstance().setLength(1000).build();
		Assert.assertEquals(1000, message.getLength());
	}

	@Test
	public void testGetPayload() {
		final byte payload[] = new byte[6];
		final Message message = MessageImple.newInstance().setPayload(payload).build();
		Assert.assertEquals(payload, message.getPayload());
	}

	@Test
	public void testGetPriority() {
		final Message message = MessageImple.newInstance().setPriority(6).build();
		Assert.assertEquals(6, message.getPriority());

	}

	@Test
	public void testGetSource() {
		final Router router = new LocalRouter();
		final Message message = MessageImple.newInstance().setSource(router).build();
		Assert.assertEquals(router, message.getSource());
	}

	@Test
	public void testSettingTTL() {
		final Message message = MessageImple.newInstance().setTtl(5).build();
		Assert.assertEquals(5, message.getTTL());
	}
}
