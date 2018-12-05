package networking.mesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class ControlMessage extends MessageImple {

	ControlMessage(Builder builder) {
		super(builder);
		setPriority(1);

	}

	protected abstract byte getControlType();

	protected abstract byte[] getMessage();

	@Override
	public byte[] getPayload() {

		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			output.write(getControlType());
			output.write(getMessage());
			final byte[] result = output.toByteArray();
			setLength(result.length);
			return result;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
