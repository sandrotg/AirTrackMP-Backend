package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.NodeRequest;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService){
        this.nodeService = nodeService;
    }
    
    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody NodeRequest request){
        Node saved = nodeService.saveNode(request);
        return ResponseEntity.ok(saved);
    }
}
