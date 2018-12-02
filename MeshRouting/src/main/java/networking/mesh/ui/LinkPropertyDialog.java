package networking.mesh.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;

import networking.mesh.Link;

public class LinkPropertyDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final Link edge;

	public LinkPropertyDialog(final JFrame parent, final Link edge)
	{
		super(parent, true);
		this.initComponents();
		this.edge = edge;
		this.setTitle("Link : " + this.edge.getID());
	}

	private void initComponents()
	{

	}
}
