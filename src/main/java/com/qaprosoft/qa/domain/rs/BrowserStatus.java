package com.qaprosoft.qa.domain.rs;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class BrowserStatus {

    @SerializedName("browser_status")
    @Expose
    private BrowserStatus_ browserStatus;

    /**
     * 
     * @return The browserStatus
     */
    public BrowserStatus_ getBrowserStatus() {
	return browserStatus;
    }

    /**
     * 
     * @param browserStatus
     *            The browser_status
     */
    public void setBrowserStatus(BrowserStatus_ browserStatus) {
	this.browserStatus = browserStatus;
    }

}