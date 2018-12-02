/*
 * DeleteVertexMenuItem.java
 *
 * Created on March 21, 2007, 2:03 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */
package networking.mesh.ui.mouseplugin;

import javax.swing.JMenuItem;

import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * A class to implement the deletion of a vertex from within a
 * PopupVertexEdgeMenuMousePlugin.
 *
 * @author Dr. Greg M. Bernstein
 */
public class DeleteVertexMenuItem<V, E> extends JMenuItem implements VertexMenuListener<V, E>
{
	private static final long serialVersionUID = 1L;
	private V vertex;
	private VisualizationViewer<V, E> visComp;

	/** Creates a new instance of DeleteVertexMenuItem */
	public DeleteVertexMenuItem()
	{
		super("Delete Vertex");
		this.addActionListener(e ->
		{
			DeleteVertexMenuItem.this.visComp.getPickedVertexState().pick(DeleteVertexMenuItem.this.vertex, false);
			DeleteVertexMenuItem.this.visComp.getGraphLayout().getGraph().removeVertex(DeleteVertexMenuItem.this.vertex);
			DeleteVertexMenuItem.this.visComp.repaint();
		});
	}

	/**
	 * Implements the VertexMenuListener interface.
	 *
	 * @param v
	 * @param visComp
	 */
	@Override
	public void setVertexAndView(final V v, final VisualizationViewer<V, E> visComp)
	{
		this.vertex = v;
		this.visComp = visComp;
		this.setText("Delete Vertex " + v.toString());
	}

}