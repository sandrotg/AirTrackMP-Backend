package com.airtrackmp.iot.airtrackmp.dto;

import com.airtrackmp.iot.airtrackmp.dto.enums.NodeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeRequest {

    private String name;
    private String location;
    private Float latitude;
    private Float longitude;
    private NodeStatus status;
}
