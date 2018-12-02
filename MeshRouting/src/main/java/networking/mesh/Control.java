package networking.mesh;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Control implements Runnable
{

	private final static Logger LOGGER = Logger.getLogger(Control.class.getName());
	private final Model model;

	public Control(final Model model)
	{
		this.model = model;
	}

	@Override
	public void run()
	{
		boolean running = true;
		try
		{
			while (running)
			{
				try
				{
					final List<Router> routers = this.model.getVertices().stream().collect(Collectors.toList());
					if (routers.size() > 2)
					{
						final int sourceIndex = (int) (Math.random() * routers.size());
						final int destinationIndex = (int) (Math.random() * routers.size());
						final Router source = routers.get(sourceIndex);
						final Router destination = routers.get(destinationIndex);
						this.sendMessage(source, destination);
					}
				} catch (final ConcurrentModificationException e)
				{
					// ignore
				}
				Thread.sleep(10 * 1000);
			}
		} catch (final InterruptedException e)
		{
			running = false;
		}
	}

	private void sendMessage(final Router source, final Router destination)
	{
		Control.LOGGER.info("Sending message from " + source + " to " + destination + ".");
		source.sendMessage(destination, 1000);
	}

}
