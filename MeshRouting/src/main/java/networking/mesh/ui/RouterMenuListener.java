package networking.mesh.ui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import networking.mesh.Link;
import networking.mesh.Router;

public interface RouterMenuListener
{
	void setRouterAndView(Router e, VisualizationViewer<Router, Link> visView);
}
