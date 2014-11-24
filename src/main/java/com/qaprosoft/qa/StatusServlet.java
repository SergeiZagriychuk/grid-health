package com.qaprosoft.qa;

import java.io.IOException;
import java.util.Iterator;
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

public class StatusServlet extends RegistryBasedServlet
{
    // private final static Logger LOGGER =
    // Logger.getLogger(StatusServlet.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -7294299154589147554L;

    public StatusServlet()
    {
	this(null);
    }

    public StatusServlet(Registry registry)
    {
	super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
	process(req, resp);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
	response.setContentType("text/json");
	response.setCharacterEncoding("UTF-8");
	JSONObject res;
	try
	{
	    if ("GET".equals(request.getMethod()))
	    {
		response.setStatus(200);
		res = getResponse();
		response.getWriter().print(res);
	    }
	    else if ("POST".equals(request.getMethod()))
	    {
		response.setStatus(200);
		res = checkSessionAvailability();
		response.getWriter().print(res);
	    }
	    else
	    {
		response.setStatus(404);
	    }
	    response.getWriter().close();
	}
	catch (JSONException e)
	{
	    e.printStackTrace();
	    response.setStatus(404);
	    response.getWriter().print(e.getMessage());
	    response.getWriter().close();
	}
	catch (GridException e)
	{
	    e.printStackTrace();
	    response.setStatus(404);
	    try
	    {
		response.getWriter().print(
			new JSONObject().put("status", new JSONObject().put("msg", "session not created").put("description", e.getMessage())));
	    }
	    catch (JSONException e1)
	    {
		response.getWriter().print(e.getMessage());
	    }
	    response.getWriter().close();
	}

    }

    private JSONObject getResponse() throws IOException, JSONException
    {
	JSONObject requestJSON = new JSONObject();
	ProxySet proxies = this.getRegistry().getAllProxies();
	Iterator<RemoteProxy> iterator = proxies.iterator();
	JSONArray busyProxies = new JSONArray();
	JSONArray freeProxies = new JSONArray();
	while (iterator.hasNext())
	{
	    RemoteProxy eachProxy = iterator.next();
	    if (eachProxy.isBusy())
	    {
		busyProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
	    }
	    else
	    {
		freeProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
	    }
	}
	requestJSON.put("BusyProxies", busyProxies);
	requestJSON.put("FreeProxies", freeProxies);

	return requestJSON;
    }

    private JSONObject checkSessionAvailability() throws IOException, JSONException
    {
	DesiredCapabilities dc = DesiredCapabilities.firefox();

	JSONObject rsJSON = new JSONObject();
	ProxySet proxies = this.getRegistry().getAllProxies();
	Iterator<RemoteProxy> iterator = proxies.iterator();
	while (iterator.hasNext())
	{
	    RemoteProxy proxy = iterator.next();
	    Registry proxyRegistry = proxy.getRegistry();

	    MockedRequestHandler mockRqHandler = GridHelper.createNewSessionHandler(proxyRegistry, convertDesiredCapbilities(dc));
	    proxyRegistry.addNewSessionRequest(mockRqHandler);
	    try
	    {
		Thread.sleep(1000);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	    TestSession session = mockRqHandler.getSession();
	    if (session == null)
	    {
		rsJSON.put("status", new JSONObject().put("msg", "session not created"));
	    }
	    else
	    {
		try
		{
		    rsJSON.put("status",
			    new JSONObject().put("msg", "session successfully created").put("description", "session key: " + session.getInternalKey()));
		}
		finally
		{
		    proxyRegistry.terminate(session, SessionTerminationReason.CLIENT_STOPPED_SESSION);
		}
	    }
	}
	return rsJSON;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertDesiredCapbilities(DesiredCapabilities dc)
    {
	return (Map<String, Object>) dc.asMap();
    }
}
