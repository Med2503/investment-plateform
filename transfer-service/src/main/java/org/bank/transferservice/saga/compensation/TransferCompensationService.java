package org.bank.transferservice.saga.compensation;


import lombok.RequiredArgsConstructor;
import org.bank.transferservice.client.AccountClient;
import org.bank.transferservice.entity.Transfer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferCompensationService {

    private final AccountClient accountClient;

    public void compensateDebit(Transfer transfer) {

        accountClient.deposit(
                transfer.getSourceAccountId(),
                transfer.getAmount()
        );
    }
}
