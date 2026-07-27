package org.bank.riskservice.rule;

import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;

public interface RiskRule {

    RiskDecision evaluate(RiskContext riskContext);
}
