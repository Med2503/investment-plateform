
package org.bank.fraudservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.entity.FraudAlertEntity;
import org.bank.fraudservice.mapper.FraudAlertResponseMapper;
import org.bank.fraudservice.model.FraudAlertResponse;
import org.bank.fraudservice.repository.FraudAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FraudAlertQueryService {

    private final FraudAlertRepository fraudAlertRepository;
    private final FraudAlertResponseMapper responseMapper;

    public FraudAlertResponse findByTradeId(UUID tradeId) {
        FraudAlertEntity entity = fraudAlertRepository.findByTradeId(tradeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No fraud alert found for trade: " + tradeId
                ));

        return responseMapper.toResponse(entity);
    }

    public Page<FraudAlertResponse> findAll(Pageable pageable) {
        return fraudAlertRepository
                .findAllByOrderByCreatedAtDesc(pageable)
                .map(responseMapper::toResponse);
    }
}
