package networking.mesh.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import networking.mesh.Link;
import networking.mesh.LinkDirection;
import networking.mesh.Message;
import networking.mesh.Model;

public class LinkView extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final LinkViewModel model;
	private final JTable table;

	LinkView(final Model model)
	{
		this.model = new LinkViewModel(model);
		this.table = new JTable(this.model);
		final JScrollPane scroll = new JScrollPane(this.table);
		this.add(scroll);
	}

	@Override
	public void invalidate()
	{
		this.model.fireTableDataChanged();
		super.invalidate();
	}

	static class LinkViewModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;
		private final Model model;

		LinkViewModel(final Model model)
		{
			this.model = model;
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex)
		{
			return String.class;
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public String getColumnName(final int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Link ID";
				case 1:
					return "Left";
				case 2:
					return "Right";
			}
			throw new IllegalArgumentException();
		}

		@Override
		public int getRowCount()
		{
			return this.model.getEdgeCount();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			final List<Link> links = new ArrayList<>(this.model.getEdges());
			Collections.sort(links);
			final Link link = links.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return link.getID();
				case 1:
					final Message leftMessage = link.getMessage(LinkDirection.Left_To_Right);
					return leftMessage != null ? leftMessage.getID() : "";
				case 2:
					final Message rightMessage = link.getMessage(LinkDirection.Right_To_Left);
					return rightMessage != null ? rightMessage.getID() : "";
			}
			throw new IllegalArgumentException();
		}

	}
}
