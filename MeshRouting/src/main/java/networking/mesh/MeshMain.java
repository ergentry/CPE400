package networking.mesh;

import javax.swing.SwingUtilities;

import networking.mesh.ui.View;

public class MeshMain {

	public static void main(final String[] args) {
		final Model model = new Model();
		final Control control = new Control(model);
		final View view = new View(model, control);
		model.setMessageListener(view.getMessageListener());
		view.pack();
		SwingUtilities.invokeLater(() -> view.setVisible(true));
		final Thread t = new Thread(control);
		t.start();
	}

}
