package com.no_broker_application.Web_UI.controller;

import com.no_broker_application.Web_UI.client.AuthServiceClient;
import com.no_broker_application.Web_UI.client.BillingSupportServiceClient;
import com.no_broker_application.Web_UI.dto.User;
import com.no_broker_application.Web_UI.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StripeRedirectController {

    private final BillingSupportServiceClient billingSupportServiceClient;
    private final AuthServiceClient authServiceClient;
    private final CurrentUserService currentUserService;

    @GetMapping("/success")
    public String success(HttpSession session) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null) {
            return "redirect:/landingPage";
        }

        Long amount = 164900L;
        Object last = session.getAttribute("lastPaymentAmount");
        if (last instanceof Long l) {
            amount = l;
        } else if (last != null) {
            try {
                amount = Long.valueOf(last.toString());
            } catch (Exception ignored) {
            }
        }

        try {
            billingSupportServiceClient.recordSuccess(Map.of(
                    "userId", user.getUserId(),
                    "amount", amount
            ));
        } catch (Exception ignored) {
        }

        try {
            authServiceClient.updateSubscription(user.getUserId(), Map.of("isSubscribed", true));
        } catch (Exception ignored) {
        }

        Object target = session.getAttribute("tagetUrl");
        if (target != null) {
            return "redirect:" + target.toString();
        }
        return "redirect:/landingPage";
    }

    @GetMapping("/cancel")
    public String cancel(HttpSession session) {
        return "redirect:/landingPage";
    }
}
