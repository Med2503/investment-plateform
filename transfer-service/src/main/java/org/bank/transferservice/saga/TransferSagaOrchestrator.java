package org.bank.transferservice.saga;


import lombok.RequiredArgsConstructor;
import org.bank.transferservice.client.AccountClient;
import org.bank.transferservice.entity.Transfer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferSagaOrchestrator {

    private final AccountClient accountClient;

    public void execute(Transfer transfer) {
        accountClient.withdraw(
                transfer.getSourceAccountId(),
                transfer.getAmount()
        );
        accountClient.deposit(
                transfer.getDestinationAccountId(),
                transfer.getAmount()
        );


    }
}
