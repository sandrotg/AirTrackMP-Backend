package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.AlertRequest;
import com.airtrackmp.iot.airtrackmp.entity.Alert;
import com.airtrackmp.iot.airtrackmp.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
public class AlertController {

    private final AlertService alertService;

    public AlertController (AlertService alertService) {
        this.alertService = alertService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Alert> createAlert (@RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.saveAlert(request));
    }

    @GetMapping("/node/{nodeId}")
    public ResponseEntity<List<Alert>> findByNodeId (@PathVariable Integer nodeId) {
        return ResponseEntity.ok(alertService.findAlertsByNodeId(nodeId));
    }

    @GetMapping("/measurement/{measurementId}")
    public ResponseEntity<Alert> findByMeasurementId (@PathVariable Integer measurementId) {
        return ResponseEntity.ok(alertService.findAlertByMeasurement(measurementId));
    }

    @GetMapping
    public ResponseEntity<List<Alert>> findAll () {
        return ResponseEntity.ok(alertService.findAllAlerts());
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<Alert> updateAlert (@PathVariable Integer alertId, @RequestBody AlertRequest request) {
        return ResponseEntity.ok(alertService.updateAlert(alertId, request));
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<String> removeAlert (@PathVariable Integer alertId) {

        alertService.removeAlert(alertId);

        return ResponseEntity.ok("Alert deleted successfully");
    }
}
