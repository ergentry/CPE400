package networking.mesh.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import networking.mesh.MessageListener;
import networking.mesh.Model;
import networking.mesh.ModelListener;

public class View extends JFrame implements ModelListener
{

	private static final long serialVersionUID = 1L;
	private final GraphPanel graph;
	private final LinkView linkList;
	private final MessageView messageList;
	private final RouterView routerList;
	private final JTabbedPane tabbedPane;

	public View(final Model model)
	{
		this.setTitle("Mesh simulator");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		model.addModelListener(this);

		this.setLayout(new BorderLayout());
		this.graph = new GraphPanel(model);
		this.add(this.graph, BorderLayout.CENTER);

		this.routerList = new RouterView(model);
		this.linkList = new LinkView(model);
		this.messageList = new MessageView(model);

		this.tabbedPane = new JTabbedPane();
		this.tabbedPane.addTab("Routers", this.routerList);
		this.tabbedPane.addTab("Links", this.linkList);
		this.tabbedPane.addTab("Messages", this.messageList);

		this.add(this.tabbedPane, BorderLayout.EAST);

		final JMenu file = new JMenu("File");
		file.add(new AbstractAction("Make Image")
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final JFileChooser chooser = new JFileChooser();
				final int option = chooser.showSaveDialog(null);
				if (option == JFileChooser.APPROVE_OPTION)
				{
					final File selectedFile = chooser.getSelectedFile();
					View.this.getGraph().writeJPEGImage(selectedFile);
				}
			}
		});

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);

		this.setJMenuBar(menuBar);
	}

	public MessageListener getMessageListener()
	{
		return this.messageList;
	}

	@Override
	public void modelUpdated()
	{
		SwingUtilities.invokeLater(() ->
		{
			this.routerList.invalidate();
			this.linkList.invalidate();
		});
	}

	GraphPanel getGraph()
	{
		return this.graph;
	}
}
