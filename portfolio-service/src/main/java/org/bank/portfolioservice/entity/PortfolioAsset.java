package org.bank.portfolioservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private UUID portfolioId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal averagePrice;
}
