package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeRepository extends JpaRepository<Node, Integer> {

    Optional<Node> findByIdAndDeletedFalse(Integer id);

}