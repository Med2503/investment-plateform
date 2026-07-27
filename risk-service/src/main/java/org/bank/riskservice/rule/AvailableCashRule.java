package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.dto.response.AccountBalanceResponse;
import org.bank.riskservice.gateway.AccountGateway;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskDecisionStatus;
import org.bank.riskservice.util.RiskDecisions;
import org.bank.sharedevents.event.trade.TradeType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvailableCashRule implements RiskRule {

    private final AccountGateway accountGateway;


    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        if (riskContext.tradeType() == TradeType.SELL) {
            return approve();
        }

        AccountBalanceResponse balance = accountGateway.getBalance(riskContext.accountId());

        if (balance.availableBalance().compareTo(riskContext.totalAmount()) < 0) {
            RiskDecisions.rejected("Insufficient balance");
        }
        return RiskDecisions.approved();

    }

    private RiskDecision approve() {
        return RiskDecisions.approved();
    }
}
