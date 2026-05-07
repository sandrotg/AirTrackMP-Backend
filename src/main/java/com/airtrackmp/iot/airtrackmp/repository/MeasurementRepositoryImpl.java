package com.airtrackmp.iot.airtrackmp.repository;

import com.airtrackmp.iot.airtrackmp.dto.MeasurementAverageDto;
import com.airtrackmp.iot.airtrackmp.entity.Measurement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class MeasurementRepositoryImpl implements MeasurementRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MeasurementAverageDto> getAverages(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to,
            String groupBy
    ){
        if(!List.of("hour", "day", "month").contains(groupBy))throw new IllegalArgumentException("invalid groupBy");

        String sql = """
            SELECT 
                DATE_TRUNC('%s', m.recorded_at) AS period,
                AVG(m.pm25),
                AVG(m.pm10),
                AVG(m.temperature),
                AVG(m.humidity)
            FROM measurement m
            WHERE m.node_id = :nodeId
              AND m.recorded_at BETWEEN :from AND :to
            GROUP BY period
            ORDER BY period
        """.formatted(groupBy);
        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("nodeId", nodeId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        return results.stream().map(obj -> new MeasurementAverageDto(
                ((Timestamp) obj[0]).toLocalDateTime(),
                ((Number) obj[1]).doubleValue(),
                ((Number) obj[2]).doubleValue(),
                ((Number) obj[3]).doubleValue(),
                ((Number) obj[4]).doubleValue()
        )).toList();
    }

    @Override
    public List<Measurement> getIntervalMeasurements(
            Integer nodeId,
            LocalDateTime from,
            LocalDateTime to
    ){
        String sql = """
    SELECT *
    FROM measurement m
    WHERE m.node_id = :nodeId
      AND m.recorded_at BETWEEN :from AND :to
    ORDER BY m.recorded_at DESC
""";
        return entityManager.createNativeQuery(sql, Measurement.class)
                .setParameter("nodeId",nodeId)
                .setParameter("from",from)
                .setParameter("to",to)
                .getResultList();
    }
}
