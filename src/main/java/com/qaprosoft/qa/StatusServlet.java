package com.qaprosoft.qa;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.SessionTerminationReason;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.mock.GridHelper;
import org.openqa.grid.internal.mock.MockedRequestHandler;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.Gson;
import com.qaprosoft.qa.domain.rq.GetStatusRequest;
import com.qaprosoft.qa.domain.rq.Node;
import com.qaprosoft.qa.domain.rs.BrowserStatus;
import com.qaprosoft.qa.domain.rs.BrowserStatus_;
import com.qaprosoft.qa.domain.rs.NodeStatus;
import com.qaprosoft.qa.domain.rs.NodeStatus_;
import com.qaprosoft.qa.domain.rs.NodesStatus;

public class StatusServlet extends RegistryBasedServlet {
    // private final static Logger LOGGER =
    // Logger.getLogger(StatusServlet.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -7294299154589147554L;

    public StatusServlet() {
	this(null);
    }

    public StatusServlet(Registry registry) {
	super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	process(req, resp);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
	response.setContentType("text/json");
	response.setCharacterEncoding("UTF-8");
	try {
	    if ("GET".equals(request.getMethod())) {
		processGet(request, response);
	    } else if ("POST".equals(request.getMethod())) {
		processPost(request, response);
	    } else {
		response.setStatus(404);
	    }
	    response.getWriter().close();
	} catch (JSONException e) {
	    e.printStackTrace();
	    response.setStatus(404);
	    response.getWriter().print(e.getMessage());
	    response.getWriter().close();
	} catch (GridException e) {
	    e.printStackTrace();
	    response.setStatus(404);
	    try {
		response.getWriter().print(
			new JSONObject().put("status", new JSONObject().put("msg", "session not created").put("description", e.getMessage())));
	    } catch (JSONException e1) {
		response.getWriter().print(e.getMessage());
	    }
	    response.getWriter().close();
	}

    }

    private void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
	JSONObject res = getNodesStatus();
	response.getWriter().print(res);
	response.setStatus(200);
    }

    private void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
	BufferedReader reader = request.getReader();
	String payload = new String();
	String line;
	try {
	    while ((line = reader.readLine()) != null) {
		payload += line;
	    }
	} finally {
	    reader.close();
	}
	GetStatusRequest getStatusRequest = new Gson().fromJson(payload, GetStatusRequest.class);
	response.getWriter().print(getNodesStatus(getStatusRequest.getNodes()));
	response.setStatus(200);
    }

    private JSONObject getNodesStatus() throws IOException, JSONException {
	JSONObject requestJSON = new JSONObject();
	ProxySet proxies = this.getRegistry().getAllProxies();
	Iterator<RemoteProxy> iterator = proxies.iterator();
	JSONArray busyProxies = new JSONArray();
	JSONArray freeProxies = new JSONArray();
	while (iterator.hasNext()) {
	    RemoteProxy eachProxy = iterator.next();
	    if (eachProxy.isBusy()) {
		busyProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
	    } else {
		freeProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
	    }
	}
	requestJSON.put("BusyProxies", busyProxies);
	requestJSON.put("FreeProxies", freeProxies);

	return requestJSON;
    }

    private String getNodesStatus(List<Node> nodes) throws IOException, JSONException {
	ProxySet proxies = this.getRegistry().getAllProxies();
	Iterator<RemoteProxy> iterator = proxies.iterator();

	NodesStatus nodesStatus = new NodesStatus();
	List<NodeStatus> nodesStatusList = new ArrayList<NodeStatus>();

	for (Node node : nodes) {
	    NodeStatus ns = new NodeStatus();
	    NodeStatus_ ns_ = new NodeStatus_();
	    ns_.setHost(node.getNode().getHost());

	    boolean isNodeFound = false;
	    while (iterator.hasNext()) {
		RemoteProxy proxy = iterator.next();
		Registry proxyRegistry = proxy.getRegistry();

		String actualHost = (String) proxy.getConfig().get("host");
		if (node.getNode().getHost().equals(actualHost)) {
		    ns_.setStatus("available");
		    List<BrowserStatus> browserStatuses = new ArrayList<BrowserStatus>();
		    for (String browser : node.getNode().getBrowsers()) {
			BrowserStatus_ browserStatus_ = new BrowserStatus_();
			browserStatus_.setBrowser(browser);

			Map<String, Object> dc = getDesiredCapbilities(browser);
			if (dc == null) {
			    browserStatus_.setStatus("fail: check browser name in request");
			    continue;
			}
			MockedRequestHandler mockRqHandler = GridHelper.createNewSessionHandler(proxyRegistry, dc);
			proxyRegistry.addNewSessionRequest(mockRqHandler);
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
			TestSession session = mockRqHandler.getSession();
			if (session == null) {
			    browserStatus_.setStatus("fail");
			} else {
			    try {
				browserStatus_.setStatus("pass");
				// "session key: " + session.getInternalKey()
			    } finally {
				proxyRegistry.terminate(session, SessionTerminationReason.CLIENT_STOPPED_SESSION);
			    }
			}
			BrowserStatus bs = new BrowserStatus();
			bs.setBrowserStatus(browserStatus_);
			browserStatuses.add(bs);
		    }
		    ns_.setBrowserStatuses(browserStatuses);
		    isNodeFound = true;
		    break;
		}
	    }

	    if (!isNodeFound) {
		ns_.setStatus("unavailable");
	    }

	    ns.setNodeStatus(ns_);
	    nodesStatusList.add(ns);
	}
	nodesStatus.setNodeStatuses(nodesStatusList);
	return new Gson().toJson(nodesStatus);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDesiredCapbilities(String browser) {
	if ("firefox".equals(browser)) {
	    return (Map<String, Object>) DesiredCapabilities.firefox().asMap();
	} else if ("chrome".equals(browser)) {
	    return (Map<String, Object>) DesiredCapabilities.chrome().asMap();
	} else if ("ie".equals(browser)) {
	    return (Map<String, Object>) DesiredCapabilities.internetExplorer().asMap();
	} else {
	    return null;
	}
    }
}
