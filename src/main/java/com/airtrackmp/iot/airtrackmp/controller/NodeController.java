package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.NodeRequest;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService){
        this.nodeService = nodeService;
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Node> createNode(@RequestBody NodeRequest request){
        Node saved = nodeService.saveNode(request);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{nodeId}/delete")
    public ResponseEntity<Node> deleteNode(@PathVariable Integer nodeId ){
        Node deleted = nodeService.deleteNode(nodeId);
        return ResponseEntity.ok(deleted);
    }

    @PutMapping("/{nodeId}")
    public ResponseEntity<Node> updateNode(@PathVariable Integer nodeId, @RequestBody NodeRequest request){
        Node updated = nodeService.updateNode(nodeId, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Node>> getAllNodes(){
        List<Node> nodes = nodeService.getAllNodes();
        return ResponseEntity.ok(nodes);
    }



}
