package fr.gouv.tac.analytics.server.service.kafka.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnalyticsDeletion {

    String installationUuid;

    Instant deletionTimestamp;
}
