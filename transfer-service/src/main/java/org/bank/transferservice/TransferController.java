package org.bank.transferservice;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.transferservice.dto.CreateTransferRequest;
import org.bank.transferservice.dto.TransferResponse;
import org.bank.transferservice.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(
            @Valid @RequestBody CreateTransferRequest request
    ) {
        return service.createTransfer(request);
    }

}
