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
	private final RouterListViewModel model;
	private final JTable table;

	RouterView(final Model model)
	{
		this.model = new RouterListViewModel(model);
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

	static class RouterListViewModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;
		Model model;

		RouterListViewModel(final Model model)
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
			return 2;
		}

		@Override
		public String getColumnName(final int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Router ID";
				case 1:
					return "Queue Size";
			}
			throw new IllegalArgumentException();
		}

		@Override
		public int getRowCount()
		{
			return this.model.getVertexCount();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			final List<Router> routers = new ArrayList<>(this.model.getVertices());
			Collections.sort(routers);
			final Router router = routers.get(rowIndex);
			switch (columnIndex)
			{
				case 0:
					return router.getID();
				case 1:
					return router.getQueue().size();
			}
			throw new IllegalArgumentException();

		}

	}
}
