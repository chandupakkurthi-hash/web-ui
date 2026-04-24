package com.no_broker_application.Web_UI.client;

import com.no_broker_application.Web_UI.dto.Transaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "billing-support-service")
public interface BillingSupportServiceClient {

    @PostMapping("/api/payment/create-checkout-session")
    String createCheckoutSession(@RequestBody Map<String, Object> requestBody);

    @PostMapping("/api/billing/transactions/success")
    Transaction recordSuccess(@RequestBody Map<String, Object> requestBody);

    @GetMapping("/api/billing/transactions/{userId}")
    List<Transaction> listTransactions(@PathVariable("userId") Long userId);

    @GetMapping("/api/billing/subscription/status")
    Map<String, Object> subscriptionStatus(@RequestParam("userId") Long userId);

    @PostMapping("/chatbot")
    String chatbot(@RequestBody Map<String, String> requestBody);
}

