package org.bank.sharedevents.event;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    private String eventType;

    private String accountId;

    private String userId;

    private Instant timestamp;

    private String details;

}