package networking.mesh.ui;

import networking.mesh.Link;

/**
 *
 *
 */
public class EdgePropertyDialog extends javax.swing.JDialog
{
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;

	/**
	 *
	 * @param parent
	 * @param edge
	 */
	public EdgePropertyDialog(final java.awt.Frame parent, final Link edge)
	{
		super(parent, true);
		this.initComponents();
		this.setTitle("Edge: " + edge.toString());
	}

	private void initComponents()
	{
		this.jButton1 = new javax.swing.JButton();
		this.jLabel1 = new javax.swing.JLabel();
		this.jLabel2 = new javax.swing.JLabel();

		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Edge Properties");
		this.jButton1.setText("OK");
		this.jButton1.addActionListener(evt -> EdgePropertyDialog.this.okButtonHandler(evt));

		this.jLabel1.setText("Capacity:");

		this.jLabel2.setText("Weight:");

		this.pack();
	}

	/**
	 * @param evt
	 */
	private void okButtonHandler(final java.awt.event.ActionEvent evt)
	{
		this.dispose();
	}

}