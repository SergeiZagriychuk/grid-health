package com.qaprosoft.qa.domain.rs;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class NodeStatus {

    @SerializedName("node_status")
    @Expose
    private NodeStatus_ nodeStatus;

    /**
     * 
     * @return The nodeStatus
     */
    public NodeStatus_ getNodeStatus() {
	return nodeStatus;
    }

    /**
     * 
     * @param nodeStatus
     *            The node_status
     */
    public void setNodeStatus(NodeStatus_ nodeStatus) {
	this.nodeStatus = nodeStatus;
    }

}