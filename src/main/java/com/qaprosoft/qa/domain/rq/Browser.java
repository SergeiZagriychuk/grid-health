package com.qaprosoft.qa.domain.rq;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Browser {

    @SerializedName("browser_name")
    @Expose
    private String browserName;
    @SerializedName("browser_version")
    @Expose
    private String browserVersion;

    /**
     * 
     * @return The browserName
     */
    public String getBrowserName() {
	return browserName;
    }

    /**
     * 
     * @param browserName
     *            The browser_name
     */
    public void setBrowserName(String browserName) {
	this.browserName = browserName;
    }

    /**
     * 
     * @return The browserVersion
     */
    public String getBrowserVersion() {
	return browserVersion;
    }

    /**
     * 
     * @param browserVersion
     *            The browser_version
     */
    public void setBrowserVersion(String browserVersion) {
	this.browserVersion = browserVersion;
    }

}