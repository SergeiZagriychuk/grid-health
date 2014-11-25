package com.qaprosoft.qa.domain.rs;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class BrowserStatus_ {

    @Expose
    private String browser;
    @Expose
    private String status;

    /**
     * 
     * @return The browser
     */
    public String getBrowser() {
	return browser;
    }

    /**
     * 
     * @param browser
     *            The browser
     */
    public void setBrowser(String browser) {
	this.browser = browser;
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

}