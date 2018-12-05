package networking.mesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class LeaderNotification extends ControlMessage {

	private static final int LEADER_NOTIFICATION_TYPE = 2;

	private volatile int leader;

	public LeaderNotification(Builder builder, int leader) {
		super(builder);
		setRouteTo(builder.getDestination());
		this.leader = leader;

	}

	@Override
	protected byte getControlType() {
		return LEADER_NOTIFICATION_TYPE;
	}

	@Override
	protected byte[] getMessage() {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			final ObjectOutputStream stream = new ObjectOutputStream(output);
			stream.writeInt(leader);
			stream.close();
			return output.toByteArray();
		} catch (final IOException e) {

			e.printStackTrace();
		}
		return null;
	}

}
