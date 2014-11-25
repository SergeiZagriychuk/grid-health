package com.qaprosoft.qa.domain.rq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class GetStatusRequest {

    @Expose
    private List<Node> nodes = new ArrayList<Node>();

    /**
     * 
     * @return The nodes
     */
    public List<Node> getNodes() {
	return nodes;
    }

    /**
     * 
     * @param nodes
     *            The nodes
     */
    public void setNodes(List<Node> nodes) {
	this.nodes = nodes;
    }

}