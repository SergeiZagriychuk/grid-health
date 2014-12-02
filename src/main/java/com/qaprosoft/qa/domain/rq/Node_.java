package com.qaprosoft.qa.domain.rq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Node_ {

    @Expose
    private String host;
    /**
     * timeout in sec used to wait until session could be created
     */
    @Expose
    private int timeout;
    @Expose
    private List<String> browsers = new ArrayList<String>();

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

    public int getTimeout() {
	return timeout;
    }

    public void setTimeout(int timeout) {
	this.timeout = timeout;
    }

    /**
     * 
     * @return The browsers
     */
    public List<String> getBrowsers() {
	return browsers;
    }

    /**
     * 
     * @param browsers
     *            The browsers
     */
    public void setBrowsers(List<String> browsers) {
	this.browsers = browsers;
    }

}