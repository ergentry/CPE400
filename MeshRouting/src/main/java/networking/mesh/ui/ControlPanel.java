package networking.mesh.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import networking.mesh.Model;

public class ControlPanel extends JPanel {

	private class LocalKeyListener implements KeyListener {

		@Override
		public void keyPressed(final KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyReleased(final KeyEvent e) {
			if (e.getSource() instanceof JTextField) {
				final JTextField field = (JTextField) e.getSource();
				field.postActionEvent();
			}

		}

		@Override
		public void keyTyped(final KeyEvent e) {
			if (e.getSource() instanceof JTextField) {
				final JTextField field = (JTextField) e.getSource();
				field.postActionEvent();
			}
		}

	}

	private static final long serialVersionUID = 1L;

	public ControlPanel(final Model model) {
		this.setLayout(new BorderLayout());

		final JTextField ttl = new JTextField(Integer.toString(model.getTimeToLive()), 10);
		ttl.addActionListener(l -> {
			try {
				final int newTTL = Integer.parseInt(ttl.getText());
				ttl.setForeground(Color.BLACK);
				model.setTimeToLive(newTTL);
			} catch (final NumberFormatException e) {
				ttl.setForeground(Color.RED);
			}
		});

		ttl.addKeyListener(new LocalKeyListener());

		final JTextField duration = new JTextField(Integer.toString((int) model.getSleepDuration()), 10);
		duration.addActionListener(l -> {
			try {
				final int newDuration = Integer.parseInt(duration.getText());
				ttl.setForeground(Color.BLACK);
				model.setSleepDuration(newDuration);
			} catch (final NumberFormatException e) {
				ttl.setForeground(Color.RED);
			}
		});

		ttl.addKeyListener(new LocalKeyListener());

		final JPanel names = new JPanel(new GridLayout(2, 1));
		names.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		final JLabel ttlLabel = new JLabel("TTL");
		ttlLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		final JLabel durationLabel = new JLabel("Sleep Duration (ms)");
		durationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		names.add(ttlLabel);
		names.add(durationLabel);

		final JPanel values = new JPanel(new GridLayout(2, 1));
		values.add(ttl);
		values.add(duration);

		final JPanel display = new JPanel(new BorderLayout());
		display.add(names, BorderLayout.WEST);
		display.add(values, BorderLayout.CENTER);

		this.add(display, BorderLayout.NORTH);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
}
