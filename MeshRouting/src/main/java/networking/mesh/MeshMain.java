package networking.mesh;

import javax.swing.SwingUtilities;

import networking.mesh.ui.View;

public class MeshMain
{

	public static void main(final String[] args)
	{
		final Model model = new Model();
		final View view = new View(model);
		model.setMessageListener(view.getMessageListener());
		view.pack();
		SwingUtilities.invokeLater(() -> view.setVisible(true));
	}

}
