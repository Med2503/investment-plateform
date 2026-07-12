package org.bank.transferservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID sourceAccountId;


    @Column(nullable = false)
    private UUID destinationAccountId;


    @Column(nullable = false, scale = 4, precision = 19)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    private Instant createdAt;
    private Instant completedAt;

    private String failureReason;
}
