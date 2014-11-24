package com.qaprosoft.qa.domain;

import java.util.List;

public class Node {
    private String host;
    private List<String> supportedBrowsers;

    public Node() {
    }

    public Node(String host, List<String> supportedBrowsers) {
	super();
	this.host = host;
	this.supportedBrowsers = supportedBrowsers;
    }

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    public List<String> getSupportedBrowsers() {
	return supportedBrowsers;
    }

    public void setSupportedBrowsers(List<String> supportedBrowsers) {
	this.supportedBrowsers = supportedBrowsers;
    }

    @Override
    public String toString() {
	return "Node [host=" + host + ", supportedBrowsers="
		+ supportedBrowsers + "]";
    }

}
