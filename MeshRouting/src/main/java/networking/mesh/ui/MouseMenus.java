/*
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package networking.mesh.ui;

import java.awt.geom.Point2D;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import networking.mesh.Link;
import networking.mesh.Router;
import networking.mesh.ui.mouseplugin.DeleteEdgeMenuItem;
import networking.mesh.ui.mouseplugin.DeleteVertexMenuItem;
import networking.mesh.ui.mouseplugin.EdgeMenuListener;
import networking.mesh.ui.mouseplugin.MenuPointListener;
import networking.mesh.ui.mouseplugin.VertexMenuListener;

public class MouseMenus
{

	public static class CapacityDisplay extends JMenuItem implements EdgeMenuListener<Router, Link>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void setEdgeAndView(final Link e, final VisualizationViewer<Router, Link> visComp)
		{
			this.setText("Capacity " + e + " = " + 500);
		}
	}

	public static class EdgeMenu extends JPopupMenu
	{
		private static final long serialVersionUID = 1L;

		// private JFrame frame;
		public EdgeMenu(final JFrame frame)
		{
			super("Edge Menu");
			// this.frame = frame;
			this.add(new DeleteEdgeMenuItem<Router, Link>());
			this.addSeparator();
			this.add(new WeightDisplay());
			this.add(new CapacityDisplay());
			this.addSeparator();
			this.add(new EdgePropItem(frame));
		}

	}

	public static class EdgePropItem extends JMenuItem implements EdgeMenuListener<Router, Link>, MenuPointListener
	{
		private static final long serialVersionUID = 1L;

		Link edge;
		Point2D point;
		VisualizationViewer<Router, Link> visComp;

		public EdgePropItem(final JFrame frame)
		{
			super("Edit Edge Properties...");
			this.addActionListener(e ->
			{
				final EdgePropertyDialog dialog = new EdgePropertyDialog(frame, EdgePropItem.this.edge);
				dialog.setLocation((int) EdgePropItem.this.point.getX() + frame.getX(), (int) EdgePropItem.this.point.getY() + frame.getY());
				dialog.setVisible(true);
			});
		}

		@Override
		public void setEdgeAndView(final Link edge, final VisualizationViewer<Router, Link> visComp)
		{
			this.edge = edge;
			this.visComp = visComp;
		}

		@Override
		public void setPoint(final Point2D point)
		{
			this.point = point;
		}

	}

	public static class pscCheckBox extends JCheckBoxMenuItem implements VertexMenuListener<Router, Link>
	{
		private static final long serialVersionUID = 1L;

		Router v;

		public pscCheckBox()
		{
			super("PSC Capable");
		}

		@Override
		public void setVertexAndView(final Router v, final VisualizationViewer<Router, Link> visComp)
		{
			this.v = v;
		}

	}

	public static class tdmCheckBox extends JCheckBoxMenuItem implements VertexMenuListener<Router, Link>
	{
		private static final long serialVersionUID = 1L;

		Router v;

		public tdmCheckBox()
		{
			super("TDM Capable");
		}

		@Override
		public void setVertexAndView(final Router v, final VisualizationViewer<Router, Link> visComp)
		{
			this.v = v;
		}
	}

	public static class VertexMenu extends JPopupMenu
	{
		private static final long serialVersionUID = 1L;

		public VertexMenu()
		{
			super("Vertex Menu");
			this.add(new DeleteVertexMenuItem<Router, Link>());
			this.addSeparator();
			this.add(new pscCheckBox());
			this.add(new tdmCheckBox());
		}
	}

	public static class WeightDisplay extends JMenuItem implements EdgeMenuListener<Router, Link>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void setEdgeAndView(final Link e, final VisualizationViewer<Router, Link> visComp)
		{
			this.setText("Weight " + e + " = " + 500);
		}
	}

}