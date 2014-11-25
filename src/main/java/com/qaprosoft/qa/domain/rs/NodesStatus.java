package com.qaprosoft.qa.domain.rs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class NodesStatus {

    @Expose
    private String message;
    @Expose
    private String description;
    @SerializedName("node_statuses")
    @Expose
    private List<NodeStatus> nodeStatuses = new ArrayList<NodeStatus>();

    /**
     * 
     * @return The message
     */
    public String getMessage() {
	return message;
    }

    /**
     * 
     * @param message
     *            The message
     */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * 
     * @return The description
     */
    public String getDescription() {
	return description;
    }

    /**
     * 
     * @param description
     *            The description
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * 
     * @return The nodeStatuses
     */
    public List<NodeStatus> getNodeStatuses() {
	return nodeStatuses;
    }

    /**
     * 
     * @param nodeStatuses
     *            The node_statuses
     */
    public void setNodeStatuses(List<NodeStatus> nodeStatuses) {
	this.nodeStatuses = nodeStatuses;
    }

}