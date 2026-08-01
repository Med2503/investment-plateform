package org.bank.riskservice.rule;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.dto.response.CustomerAMLResponse;
import org.bank.riskservice.gateway.CustomerAMLGateway;
import org.bank.riskservice.model.AMLStatus;
import org.bank.riskservice.model.RiskContext;
import org.bank.riskservice.model.RiskDecision;
import org.bank.riskservice.model.RiskRejectionReason;
import org.bank.riskservice.util.RiskDecisions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(RiskRuleOrder.AML)
@RequiredArgsConstructor
public class AMLRule implements RiskRule {

    private final CustomerAMLGateway amlGateway;

    @Override
    public RiskDecision evaluate(RiskContext riskContext) {
        CustomerAMLResponse customerAML =
                amlGateway.getAMLStatus(
                        riskContext.userId()
                );


        if (customerAML.amlStatus()
                == AMLStatus.BLOCKED) {


            return RiskDecisions.rejected(

                    RiskRejectionReason.AML_BLOCKED,

                    "Customer blocked by AML"

            );

        }
        if (customerAML.amlStatus()
                == AMLStatus.REVIEW_REQUIRED) {


            return RiskDecisions.rejected(

                    RiskRejectionReason.AML_BLOCKED,

                    "Customer requires AML review"

            );

        }
        if (riskContext.totalAmount()
                .compareTo(
                        customerAML.dailyTransactionLimit()
                ) > 0) {


            return RiskDecisions.rejected(

                    RiskRejectionReason.AML_LIMIT_EXCEEDED,

                    "AML transaction limit exceeded"

            );

        }


        return RiskDecisions.approved();

    }


}
