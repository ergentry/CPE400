package networking.mesh.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import networking.mesh.Model;
import networking.mesh.Router;

public class RouterView extends JPanel
{
	private static final long serialVersionUID = 1L;
	NodeListViewModel model;
	JTable table;

	RouterView(final Model model)
	{
		this.model = new NodeListViewModel(model);
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

	static class NodeListViewModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;
		Model model;

		NodeListViewModel(final Model model)
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
			return 1;
		}

		@Override
		public String getColumnName(final int columnIndex)
		{
			return "Node ID";
		}

		@Override
		public int getRowCount()
		{
			return this.model.getVertexCount();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			final List<Router> nodes = new ArrayList<>(this.model.getVertices());
			Collections.sort(nodes);
			return nodes.get(rowIndex);
		}

	}
}
