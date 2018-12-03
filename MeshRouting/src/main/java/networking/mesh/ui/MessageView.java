package networking.mesh.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import networking.mesh.Message;
import networking.mesh.MessageListener;
import networking.mesh.Model;

public class MessageView extends JPanel implements MessageListener {
	static class MessageViewModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final Model model;

		MessageViewModel(final Model model) {
			this.model = model;
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "ID";
			case 1:
				return "Source";
			case 2:
				return "Destination";
			case 3:
				return "State";
			default:
				throw new RuntimeException("Unknown column index " + columnIndex);
			}
		}

		@Override
		public int getRowCount() {
			return this.model.getMessages().size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final List<Message> messages = new ArrayList<>(this.model.getMessages());
			Collections.reverse(messages);
			final Message message = messages.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return message.getID();
			case 1:
				return message.getSource().getID();
			case 2:
				return message.getDestination().getID();
			case 3:
				return message.getMessageState().name().toLowerCase();
			default:
				throw new RuntimeException("Unknown column index " + columnIndex);
			}
		}

	}

	private static final long serialVersionUID = 1L;
	MessageViewModel model;

	JTable table;

	MessageView(final Model model) {
		this.setLayout(new BorderLayout());
		this.model = new MessageViewModel(model);
		this.table = new JTable(this.model);
		final JScrollPane scroll = new JScrollPane(this.table);
		this.add(scroll, BorderLayout.CENTER);
	}

	@Override
	public void messageStateChanged(final Message message) {
		if (message.getMessageState().isTerminal()) {
			message.removeMessageListener(this);
		}
		this.model.fireTableDataChanged();
	}
}
