/*
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package networking.mesh.ui;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import networking.mesh.Link;
import networking.mesh.Router;
import networking.mesh.ui.mouseplugin.DeleteEdgeMenuItem;
import networking.mesh.ui.mouseplugin.DeleteVertexMenuItem;
import networking.mesh.ui.mouseplugin.EdgeMenuListener;
import networking.mesh.ui.mouseplugin.VertexMenuListener;

public class MouseMenus {

	public static class CapacityDisplay extends JMenuItem implements EdgeMenuListener<Router, Link> {
		private static final long serialVersionUID = 1L;

		@Override
		public void setEdgeAndView(final Link e, final VisualizationViewer<Router, Link> visComp) {
			this.setText("Capacity " + e + " = " + 500);
		}
	}

	public static class EdgeMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		// private JFrame frame;
		public EdgeMenu(final JFrame frame) {
			super("Edge Menu");
			// this.frame = frame;
			this.add(new DeleteEdgeMenuItem<Router, Link>());
			this.addSeparator();
			this.add(new StartStopLink());
		}

	}

	public static class StartStopLink extends JMenuItem implements EdgeMenuListener<Router, Link> {

		private static final long serialVersionUID = 1L;
		private volatile Link e;

		public StartStopLink() {
			super("Stop");
			this.addActionListener(l -> {
				if (e.isRunning()) {
					e.stop();
				} else {
					e.start();
				}
			});
		}

		@Override
		public void setEdgeAndView(final Link e, final VisualizationViewer<Router, Link> visView) {
			this.e = e;
			this.setText(this.e.isRunning() ? "Stop" : "Start");

		}

	}

	public static class StartStopRouter extends JMenuItem implements VertexMenuListener<Router, Link> {
		private static final long serialVersionUID = 1L;

		private volatile Router v;

		public StartStopRouter() {
			super("Stop");
			this.addActionListener(l -> {
				if (this.v.isRunning()) {
					v.stop();
				} else {
					v.start();
				}
			});
		}

		@Override
		public void setVertexAndView(final Router v, final VisualizationViewer<Router, Link> visComp) {
			this.v = v;
			this.setText(this.v.isRunning() ? "Stop" : "Start");
		}
	}

	public static class VertexMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public VertexMenu() {
			super("Vertex Menu");
			this.add(new DeleteVertexMenuItem<Router, Link>());
			this.addSeparator();
			this.add(new StartStopRouter());
		}
	}

	public static class WeightDisplay extends JMenuItem implements EdgeMenuListener<Router, Link> {
		private static final long serialVersionUID = 1L;

		@Override
		public void setEdgeAndView(final Link e, final VisualizationViewer<Router, Link> visComp) {
			this.setText("Weight " + e + " = " + 500);
		}
	}

}