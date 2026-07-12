package org.bank.transferservice.saga;


import lombok.RequiredArgsConstructor;
import org.bank.transferservice.client.AccountClient;
import org.bank.transferservice.entity.Transfer;
import org.bank.transferservice.saga.compensation.TransferCompensationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferSagaOrchestrator {

    private final AccountClient accountClient;
    private final TransferCompensationService compensationService;

    public void execute(Transfer transfer) {

        boolean debitCompleted = false;
        try {
            accountClient.withdraw(
                    transfer.getSourceAccountId(),
                    transfer.getAmount()
            );

            debitCompleted = true;

            accountClient.deposit(
                    transfer.getDestinationAccountId(),
                    transfer.getAmount()
            );
        } catch (Exception e) {
            if (debitCompleted) {
                compensationService.compensateDebit(transfer);
            }
            throw e;
        }


    }
}
