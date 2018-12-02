/*
 * DeleteEdgeMenuItem.java
 *
 * Created on March 21, 2007, 2:47 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package networking.mesh.ui.mouseplugin;

import javax.swing.JMenuItem;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A class to implement the deletion of an edge from within a
 * PopupVertexEdgeMenuMousePlugin.
 *
 * @author Dr. Greg M. Bernstein
 */
public class DeleteEdgeMenuItem<V, E> extends JMenuItem implements EdgeMenuListener<V, E>
{
	private static final long serialVersionUID = 1L;
	private E edge;
	private VisualizationViewer<V, E> visComp;

	/** Creates a new instance of DeleteEdgeMenuItem */
	public DeleteEdgeMenuItem()
	{
		super("Delete Edge");
		this.addActionListener(e ->
		{
			DeleteEdgeMenuItem.this.visComp.getPickedEdgeState().pick(DeleteEdgeMenuItem.this.edge, false);
			DeleteEdgeMenuItem.this.visComp.getGraphLayout().getGraph().removeEdge(DeleteEdgeMenuItem.this.edge);
			DeleteEdgeMenuItem.this.visComp.repaint();
		});
	}

	/**
	 * Implements the EdgeMenuListener interface to update the menu item with info
	 * on the currently chosen edge.
	 *
	 * @param edge
	 * @param visComp
	 */
	@Override
	public void setEdgeAndView(final E edge, final VisualizationViewer<V, E> visComp)
	{
		this.edge = edge;
		this.visComp = visComp;
		this.setText("Delete Edge " + edge.toString());
	}

}
