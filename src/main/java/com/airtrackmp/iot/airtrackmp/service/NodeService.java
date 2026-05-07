package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.NodeRequest;
import com.airtrackmp.iot.airtrackmp.dto.enums.NodeStatus;
import com.airtrackmp.iot.airtrackmp.entity.Node;
import com.airtrackmp.iot.airtrackmp.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NodeService {

    private final NodeRepository nodeRepo;

    public NodeService(NodeRepository nodeRepo){
        this.nodeRepo = nodeRepo;
    }

    public Node saveNode(NodeRequest request){
        Node node = Node.builder()
                .name(request.getName())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .deleted(false)
                .status(NodeStatus.CALIBRATION)
                .createdAt(LocalDateTime.now())
                .build();

        return nodeRepo.save(node);
    }

    public Node updateNode(Integer id, NodeRequest request){
        Node node = getNodeById(id);

        if(request.getName() != null) node.setName(request.getName());

        if(request.getLocation() != null) node.setLocation(request.getLocation());

        if(request.getLatitude() != null) node.setLatitude(request.getLatitude());

        if(request.getLongitude() != null) node.setLongitude(request.getLongitude());

        if(request.getStatus() != null) node.setStatus(request.getStatus());

        return nodeRepo.save(node);
    }

    public Node deleteNode(Integer id){
        Node node = getNodeById(id);

        node.setDeleted(true);
        return nodeRepo.save(node);
    }

    public List<Node> getAllNodes(){ return nodeRepo.findAll().stream().filter(
            (n)->!n.isDeleted()
            ).toList();
    }

    public Node getNodeById(Integer id){
        Node node = nodeRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Node not Found")
        );
        if(node.isDeleted()) throw new RuntimeException("Node is deleted");
        return node;
    }

}
