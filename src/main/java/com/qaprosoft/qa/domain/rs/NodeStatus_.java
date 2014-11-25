package com.qaprosoft.qa.domain.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class NodeStatus_ {

    @Expose
    private String host;
    @Expose
    private String status;
    @SerializedName("browser_statuses")
    @Expose
    private List<BrowserStatus> browserStatuses = new ArrayList<BrowserStatus>();

    /**
     * 
     * @return The host
     */
    public String getHost() {
	return host;
    }

    /**
     * 
     * @param host
     *            The host
     */
    public void setHost(String host) {
	this.host = host;
    }

    /**
     * 
     * @return The status
     */
    public String getStatus() {
	return status;
    }

    /**
     * 
     * @param status
     *            The status
     */
    public void setStatus(String status) {
	this.status = status;
    }

    /**
     * 
     * @return The browserStatuses
     */
    public List<BrowserStatus> getBrowserStatuses() {
	return browserStatuses;
    }

    /**
     * 
     * @param browserStatuses
     *            The browser_statuses
     */
    public void setBrowserStatuses(List<BrowserStatus> browserStatuses) {
	this.browserStatuses = browserStatuses;
    }

}