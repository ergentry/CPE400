package networking.mesh;

import java.security.SecureRandom;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Control implements Runnable {

	private final static Logger LOGGER = LogManager.getLogger(Control.class.getName());
	private final Model model;
	private final SecureRandom random;
	private volatile boolean paused;

	public Control(final Model model) {
		this.model = model;
		this.random = new SecureRandom();
		this.paused = false;
	}

	public boolean isPaused() {
		return paused;
	}

	@Override
	public void run() {
		boolean running = true;
		try {
			while (running) {
				if (!paused) {
					try {
						final List<Router> routers = this.model.getVertices().stream().collect(Collectors.toList());
						if (routers.size() > 1) {
							final int sourceIndex = random.nextInt(routers.size());
							final Router source = routers.get(sourceIndex);
							Router destination = source;
							while (destination.equals(source)) {
								final int destinationIndex = random.nextInt(routers.size());
								destination = routers.get(destinationIndex);
							}
							this.sendMessage(source, destination);
						}
					} catch (final ConcurrentModificationException e) {
						// ignore
					}
				}
				Thread.sleep(model.getSleepDuration());
			}
		} catch (final InterruptedException e) {
			running = false;
		}
	}

	private void sendMessage(final Router source, final Router destination) {
		Control.LOGGER.debug("Sending message from " + source + " to " + destination + ".");
		source.sendMessage(destination, 1000);
	}

	public void setPaused(final boolean paused) {
		this.paused = paused;
	}

}
