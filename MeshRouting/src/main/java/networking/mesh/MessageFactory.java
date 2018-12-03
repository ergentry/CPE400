package networking.mesh;

public interface MessageFactory {
	int getTimeToLive();

	Message newControlMessage(Router source, Router destination);

	Message newDataMessage(Router source, Router destination, int size);

	void setMessageListener(MessageListener listener);

	void setTimeToLive(int ttl);
}
