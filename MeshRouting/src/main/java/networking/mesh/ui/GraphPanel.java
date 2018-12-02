package networking.mesh.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JPanel;

import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import networking.mesh.Link;
import networking.mesh.Model;
import networking.mesh.Router;
import networking.mesh.ui.mouseplugin.PopupVertexEdgeMenuMousePlugin;

//http://www.grotto-networking.com/JUNG/MouseMenu/EditorMouseMenu.java
public class GraphPanel extends JPanel
{
	public static final int EDGE_LENGTH = 100;

	private static final Logger LOGGER = Logger.getLogger(GraphPanel.class.getName());
	private static final long serialVersionUID = 1L;
	final EditingModalGraphMouse<Router, Link> graphMouse;
	Timer timer;
	private final AbstractLayout<Router, Link> layout;
	private final Model model;

	private final VisualizationViewer<Router, Link> vv;

	public GraphPanel(final Model model)
	{
		GraphPanel.LOGGER.info("Create panel");
		this.model = model;

		this.layout = new StaticLayout<>(this.model, new Dimension(600, 600));

		this.vv = new VisualizationViewer<>(this.layout);
		this.vv.setBackground(Color.WHITE);

		this.vv.getRenderContext().setEdgeDrawPaintTransformer(l ->
		{
			return l.inUse() ? Color.RED : Color.BLACK;
		});
		this.vv.getRenderContext().setVertexLabelTransformer(MapTransformer.<Router, String>getInstance(LazyMap.<Router, String>decorate(new HashMap<Router, String>(), new ToStringLabeller<Router>())));
		this.vv.getRenderContext().setEdgeLabelTransformer(MapTransformer.<Link, String>getInstance(LazyMap.<Link, String>decorate(new HashMap<Link, String>(), new ToStringLabeller<Link>())));
		this.vv.setVertexToolTipTransformer(this.vv.getRenderContext().getVertexLabelTransformer());

		this.setLayout(new BorderLayout());
		this.setBackground(java.awt.Color.lightGray);
		this.setFont(new Font("Serif", Font.PLAIN, 12));

		this.graphMouse = new EditingModalGraphMouse<>(this.vv.getRenderContext(), model.getNodeFactory(), model.getLinkFactory());
		this.vv.setGraphMouse(this.graphMouse);
		this.graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

		final PopupVertexEdgeMenuMousePlugin<Router, Link> plugin = new PopupVertexEdgeMenuMousePlugin<>();
		plugin.setEdgePopup(new MouseMenus.EdgeMenu(null));
		plugin.setVertexPopup(new MouseMenus.VertexMenu());
		this.graphMouse.remove(this.graphMouse.getPopupEditingPlugin());
		this.graphMouse.add(plugin);

		this.add(this.vv);

		this.timer = new Timer();
		this.timer.schedule(new RemindTask(), 10, 10); // subsequent rate
	}

	public JMenu getModeMenu()
	{
		return this.graphMouse.getModeMenu();
	}

	/**
	 * copy the visible part of the graph to a file as a jpeg image
	 *
	 * @param file
	 */
	public void writeJPEGImage(final File file)
	{
		final int width = this.vv.getWidth();
		final int height = this.vv.getHeight();

		final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = bi.createGraphics();
		this.vv.paint(graphics);
		graphics.dispose();

		try
		{
			ImageIO.write(bi, "jpeg", file);
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	void process()
	{
		this.vv.repaint();
	}

	class RemindTask extends TimerTask
	{

		@Override
		public void run()
		{
			GraphPanel.this.process();
		}
	}
}
