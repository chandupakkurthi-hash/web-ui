package com.no_broker_application.Web_UI.controller;

import com.no_broker_application.Web_UI.client.BillingSupportServiceClient;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BillingSupportProxyController {

    private final BillingSupportServiceClient billingSupportServiceClient;

    @PostMapping("/api/payment/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestBody Map<String, Object> requestBody,
                                                        HttpSession session) {
        Object price = requestBody.get("price");
        if (price != null) {
            try {
                session.setAttribute("lastPaymentAmount", Long.valueOf(price.toString()));
            } catch (Exception ignored) {
            }
        }
        return ResponseEntity.ok(billingSupportServiceClient.createCheckoutSession(requestBody));
    }

    @PostMapping("/chatbot")
    public ResponseEntity<String> chatbot(@RequestBody Map<String, String> requestBody) {
        return ResponseEntity.ok(billingSupportServiceClient.chatbot(requestBody));
    }
}
