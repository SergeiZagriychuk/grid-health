package com.qaprosoft.qa;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.SessionTerminationReason;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.Gson;
import com.qaprosoft.qa.domain.BrowserName;
import com.qaprosoft.qa.domain.rq.Browser;
import com.qaprosoft.qa.domain.rq.GetStatusRequest;
import com.qaprosoft.qa.domain.rq.Node;
import com.qaprosoft.qa.domain.rs.BrowserStatus;
import com.qaprosoft.qa.domain.rs.BrowserStatus_;
import com.qaprosoft.qa.domain.rs.NodeStatus;
import com.qaprosoft.qa.domain.rs.NodeStatus_;
import com.qaprosoft.qa.domain.rs.NodesStatus;
import com.qaprosoft.qa.util.SleepUtil;

public class StatusServlet extends RegistryBasedServlet {
    private final static Logger LOGGER = Logger.getLogger(StatusServlet.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -7294299154589147554L;

    private final static int SLEEP_TIME_IN_SEC = 1;
    private final static int DEFAULT_TIMEOUT_SEC = 1;

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
		response.getWriter().print(new JSONObject().put(Constants.ERROR_KEY, e.getMessage()));
	    } catch (JSONException e1) {
		response.getWriter().print(e.getMessage());
	    }
	    response.getWriter().close();
	}

    }

    private void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
	response.getWriter().print(getProxiesInfo());
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

    private String getProxiesInfo() {
	ProxySet proxies = this.getRegistry().getAllProxies();
	Iterator<RemoteProxy> iterator = proxies.iterator();
	JSONArray jsonArray = new JSONArray();
	while (iterator.hasNext()) {
	    jsonArray.put(iterator.next().getOriginalRegistrationRequest().getAssociatedJSON());
	}
	return new Gson().toJson(jsonArray);
    }

    private String getNodesStatus(List<Node> nodes) throws IOException, JSONException {
	NodesStatus nodesStatus = new NodesStatus();
	List<NodeStatus> nodesStatusList = new ArrayList<NodeStatus>();

	for (Node node : nodes) {
	    NodeStatus ns = new NodeStatus();
	    NodeStatus_ ns_ = new NodeStatus_();
	    ns_.setHost(node.getNode().getHost());

	    ProxySet proxies = this.getRegistry().getAllProxies();
	    Iterator<RemoteProxy> iterator = proxies.iterator();
	    boolean isNodeFound = false;
	    while (iterator.hasNext()) {
		RemoteProxy proxy = iterator.next();
		String actualHost = (String) proxy.getConfig().get(Constants.HOST_KEY);
		String actualRemoteHost = (String) proxy.getConfig().get(Constants.REMOTE_HOST_KEY);
		if (node.getNode().getHost().equals(actualHost) || node.getNode().getHost().equals(actualRemoteHost)) {
		    ns_.setStatus(Constants.NODE_STATUS_AVAILABLE);
		    List<BrowserStatus> browserStatuses = new ArrayList<BrowserStatus>();
		    for (Browser browser : node.getNode().getBrowsers()) {
			BrowserStatus_ browserStatus_ = new BrowserStatus_();
			browserStatus_.setBrowser(browser.getBrowserName());

			Map<String, Object> dc = getDesiredCapabilities(browser.getBrowserName(), browser.getBrowserVersion());
			if (dc == null) {
			    browserStatus_.setStatus(Constants.STATUS_FAIL);
			    browserStatus_.setDetails(Constants.DETAILS_CHECK_BROWSER + Arrays.toString(BrowserName.values()));
			} else {
			    boolean isBrowserVersionValid = false;

			    int timeout = node.getNode().getTimeout();
			    if (timeout <= 0) {
				timeout = DEFAULT_TIMEOUT_SEC;
			    }
			    int currentSec = 0;
			    String actualVersion = null;
			    TestSession session = null;
			    while (currentSec < timeout) {
				SleepUtil.sleep(SLEEP_TIME_IN_SEC);
				currentSec++;
				session = proxy.getNewSession(dc);
				if (session != null) {
				    isBrowserVersionValid = true;
				    break;
				} else {
				    session = proxy.getNewSession(getDesiredCapabilities(browser.getBrowserName(), null));
				}
				if (session != null) {
				    actualVersion = session.getSlot().getCapabilities().get(Constants.VERSION_KEY).toString();
				    break;
				}
			    }
			    if (session == null) {
				browserStatus_.setStatus(Constants.STATUS_NOT_SUPPORTED);
				browserStatus_.setDetails(String.format("browser with name '%s' not supported on node", dc.get(Constants.BROWSER_NAME_KEY)));
			    } else {
				if (isBrowserVersionValid) {
				    browserStatus_.setStatus(Constants.STATUS_PASS);
				    // getting of browser maxInstances value
				    Iterator<DesiredCapabilities> dcIterator = proxy.getOriginalRegistrationRequest().getCapabilities().iterator();
				    while (dcIterator.hasNext()) {
					DesiredCapabilities actualDC = dcIterator.next();
					if (dc.get(Constants.BROWSER_NAME_KEY).equals(actualDC.getBrowserName())) {
					    Object maxInst = actualDC.getCapability(Constants.MAX_INSTANCES_KEY);
					    if (maxInst != null) {
						browserStatus_.setMaxInstances(maxInst.toString());
					    }
					    break;
					}
				    }
				} else {
				    browserStatus_.setStatus(Constants.STATUS_NOT_SUPPORTED_VERSION);
				    browserStatus_.setDetails(Constants.DETAILS_SUPPORTED_VERSION + actualVersion);
				}
				getRegistry().terminate(session, SessionTerminationReason.CLIENT_STOPPED_SESSION);
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
		ns_.setStatus(Constants.NODE_STATUS_UNAVAILABLE);
	    }

	    ns.setNodeStatus(ns_);
	    nodesStatusList.add(ns);
	}
	nodesStatus.setNodeStatuses(nodesStatusList);
	return new Gson().toJson(nodesStatus);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDesiredCapabilities(String browser, String version) {
	BrowserName browserName;
	try {
	    browserName = BrowserName.valueOf(browser);
	} catch (IllegalArgumentException e) {
	    return null;
	}

	DesiredCapabilities dc;
	switch (browserName) {
	case firefox:
	    dc = DesiredCapabilities.firefox();
	    break;
	case chrome:
	    dc = DesiredCapabilities.chrome();
	    break;
	case ie:
	    dc = DesiredCapabilities.internetExplorer();
	    String ieBrowserName = System.getProperty(Constants.IE_BROWSER_NAME_KEY);
	    LOGGER.info("IE browser name: " + ieBrowserName);
	    if (ieBrowserName != null) {
		dc.setBrowserName(ieBrowserName);
	    }
	    break;
	case safari:
	    dc = DesiredCapabilities.safari();
	    break;
	default:
	    return null;
	}
	dc.setVersion(version);
	return (Map<String, Object>) dc.asMap();
    }
}
