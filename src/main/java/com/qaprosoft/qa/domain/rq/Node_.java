package com.qaprosoft.qa.domain.rq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Node_ {

    @Expose
    private String host;
    @Expose
    private Integer timeout;
    @Expose
    private List<Browser> browsers = new ArrayList<Browser>();

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
     * @return The timeout
     */
    public Integer getTimeout() {
	return timeout;
    }

    /**
     * 
     * @param timeout
     *            The timeout
     */
    public void setTimeout(Integer timeout) {
	this.timeout = timeout;
    }

    /**
     * 
     * @return The browsers
     */
    public List<Browser> getBrowsers() {
	return browsers;
    }

    /**
     * 
     * @param browsers
     *            The browsers
     */
    public void setBrowsers(List<Browser> browsers) {
	this.browsers = browsers;
    }

}