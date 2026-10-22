
package org.bank.fraudservice.controller;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.model.FraudAlertResponse;
import org.bank.fraudservice.service.FraudAlertQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertQueryService queryService;

    @GetMapping("/trade/{tradeId}")
    public FraudAlertResponse findByTradeId(@PathVariable UUID tradeId) {
        return queryService.findByTradeId(tradeId);
    }

    @GetMapping
    public Page<FraudAlertResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return queryService.findAll(pageable);
    }
}
