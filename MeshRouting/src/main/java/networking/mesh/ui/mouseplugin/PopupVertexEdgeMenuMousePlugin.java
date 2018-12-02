/*
 * PopupVertexEdgeMenuMousePlugin.java
 *
 * Created on March 21, 2007, 12:56 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package networking.mesh.ui.mouseplugin;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

/**
 * A GraphMousePlugin that brings up distinct popup menus when an edge or vertex
 * is appropriately clicked in a graph. If these menus contain components that
 * implement either the EdgeMenuListener or VertexMenuListener then the
 * corresponding interface methods will be called prior to the display of the
 * menus (so that they can display context sensitive information for the edge or
 * vertex).
 *
 * @author Dr. Greg M. Bernstein
 */
public class PopupVertexEdgeMenuMousePlugin<V, E> extends AbstractPopupGraphMousePlugin
{
	private JPopupMenu edgePopup, vertexPopup;

	/** Creates a new instance of PopupVertexEdgeMenuMousePlugin */
	public PopupVertexEdgeMenuMousePlugin()
	{
		this(InputEvent.BUTTON3_MASK);
	}

	/**
	 * Creates a new instance of PopupVertexEdgeMenuMousePlugin
	 *
	 * @param modifiers mouse event modifiers see the jung visualization Event
	 *                  class.
	 */
	public PopupVertexEdgeMenuMousePlugin(final int modifiers)
	{
		super(modifiers);
	}

	/**
	 * Getter for the edge popup.
	 *
	 * @return
	 */
	public JPopupMenu getEdgePopup()
	{
		return this.edgePopup;
	}

	/**
	 * Getter for the vertex popup.
	 *
	 * @return
	 */
	public JPopupMenu getVertexPopup()
	{
		return this.vertexPopup;
	}

	/**
	 * Setter for the Edge popup.
	 *
	 * @param edgePopup
	 */
	public void setEdgePopup(final JPopupMenu edgePopup)
	{
		this.edgePopup = edgePopup;
	}

	/**
	 * Setter for the vertex popup.
	 *
	 * @param vertexPopup
	 */
	public void setVertexPopup(final JPopupMenu vertexPopup)
	{
		this.vertexPopup = vertexPopup;
	}

	/**
	 * Implementation of the AbstractPopupGraphMousePlugin method. This is where the
	 * work gets done. You shouldn't have to modify unless you really want to...
	 *
	 * @param e
	 */
	@Override
	protected void handlePopup(final MouseEvent e)
	{
		@SuppressWarnings("unchecked")
		final VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e.getSource();
		final Point2D p = e.getPoint();

		final GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
		if (pickSupport != null)
		{
			final V v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
			if (v != null)
			{
				// System.out.println("Vertex " + v + " was right clicked");
				this.updateVertexMenu(v, vv, p);
				this.vertexPopup.show(vv, e.getX(), e.getY());
			} else
			{
				final E edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
				if (edge != null)
				{
					// System.out.println("Edge " + edge + " was right clicked");
					this.updateEdgeMenu(edge, vv, p);
					this.edgePopup.show(vv, e.getX(), e.getY());

				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateEdgeMenu(final E edge, final VisualizationViewer<V, E> vv, final Point2D point)
	{
		if (this.edgePopup == null)
		{
			return;
		}
		final Component[] menuComps = this.edgePopup.getComponents();
		for (final Component comp : menuComps)
		{
			if (comp instanceof EdgeMenuListener)
			{
				((EdgeMenuListener<V, E>) comp).setEdgeAndView(edge, vv);
			}
			if (comp instanceof MenuPointListener)
			{
				((MenuPointListener) comp).setPoint(point);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateVertexMenu(final V v, final VisualizationViewer<V, E> vv, final Point2D point)
	{
		if (this.vertexPopup == null)
		{
			return;
		}
		final Component[] menuComps = this.vertexPopup.getComponents();
		for (final Component comp : menuComps)
		{
			if (comp instanceof VertexMenuListener)
			{
				((VertexMenuListener<V, E>) comp).setVertexAndView(v, vv);
			}
			if (comp instanceof MenuPointListener)
			{
				((MenuPointListener) comp).setPoint(point);
			}
		}

	}

}
