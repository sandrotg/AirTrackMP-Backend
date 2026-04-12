package com.airtrackmp.iot.airtrackmp.repository;


import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer>, MeasurementRepositoryCustom {

    List<Measurement> findByNodeIdOrderByRecordedAtDesc(Integer nodeId);

    List<Measurement> findTop10ByNodeIdOrderByRecordedAtDesc(Integer nodeId);

}