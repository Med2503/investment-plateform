package org.bank.fraudservice.rules;

import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;

import java.util.Optional;

public interface FraudRule {

    Optional<FraudReason> evaluate(FraudContext context);

    int score();
}
