package com.airtrackmp.iot.airtrackmp.dto;

import java.util.List;

public class NodeBulkMeasurementRequest {

    private Integer nodeId;
    private List<NodeMeasurementRequest> measurements;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public List<NodeMeasurementRequest> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<NodeMeasurementRequest> measurements) {
        this.measurements = measurements;
    }
}
