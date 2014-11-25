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