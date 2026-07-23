package org.bank.tradingservice.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "portfolio-client")
public interface PortfolioClient {




}
