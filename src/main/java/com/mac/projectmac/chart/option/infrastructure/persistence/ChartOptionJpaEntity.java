package com.mac.projectmac.chart.option.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.mac.projectmac.chart.option.domain.model.ChartOption;
import com.mac.projectmac.chart.option.domain.model.ChartType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "chart_option")
public class ChartOptionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chart_option_id")
    private Long chartOptionId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "chart_type", nullable = false, length = 50)
    private ChartType chartType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_mapping", columnDefinition = "json")
    private JsonNode dataMapping;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "style_option", columnDefinition = "json")
    private JsonNode styleOption;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private ChartOptionJpaEntity(Long projectId, ChartType chartType, JsonNode dataMapping, JsonNode styleOption) {
        this.projectId = projectId;
        this.chartType = chartType;
        this.dataMapping = dataMapping;
        this.styleOption = styleOption;
    }

    public static ChartOptionJpaEntity from(ChartOption chartOption) {
        ChartOptionJpaEntity entity = new ChartOptionJpaEntity(
                chartOption.getProjectId(),
                chartOption.getChartType(),
                chartOption.getDataMapping(),
                chartOption.getStyleOption()
        );
        entity.chartOptionId = chartOption.getChartOptionId();
        entity.createdAt = chartOption.getCreatedAt();
        entity.updatedAt = chartOption.getUpdatedAt();
        entity.deletedAt = chartOption.getDeletedAt();
        return entity;
    }

    public ChartOption toDomain() {
        return ChartOption.restore(
                chartOptionId,
                projectId,
                chartType,
                dataMapping,
                styleOption,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
