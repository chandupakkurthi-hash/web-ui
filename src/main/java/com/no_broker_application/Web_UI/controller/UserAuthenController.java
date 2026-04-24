package com.no_broker_application.Web_UI.controller;

import com.no_broker_application.Web_UI.client.AuthServiceClient;
import com.no_broker_application.Web_UI.client.BillingSupportServiceClient;
import com.no_broker_application.Web_UI.client.PropertyServiceClient;
import com.no_broker_application.Web_UI.dto.AuthRequest;
import com.no_broker_application.Web_UI.dto.AuthResponse;
import com.no_broker_application.Web_UI.dto.User;
import com.no_broker_application.Web_UI.security.CurrentUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserAuthenController {

    private final AuthServiceClient authServiceClient;
    private final BillingSupportServiceClient billingSupportServiceClient;
    private final PropertyServiceClient propertyServiceClient;
    private final CurrentUserService currentUserService;

    @GetMapping("/saveUser")
    public String saveUser(OAuth2AuthenticationToken authentication,
                           Model model,
                           HttpSession session,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        String email = authentication.getPrincipal().getAttribute("email");
        String name = authentication.getPrincipal().getAttribute("name");
        String picture = authentication.getPrincipal().getAttribute("picture");

        AuthRequest authRequest = new AuthRequest(email, name, picture);
        AuthResponse authResponse = authServiceClient.googleLogin(authRequest);

        Cookie jwtCookie = new Cookie("jwt_token", authResponse.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);

        return "redirect:/landingPage";
    }

    @GetMapping("/checkRoles")
    @ResponseBody
    public String checkUserRoles(Authentication authentication) {
        return "Roles: " + authentication.getAuthorities();
    }

    @GetMapping("/subscribeOrNot")
    @ResponseBody
    public Map<String, Boolean> checkSubscription(HttpSession session) {
        User user = currentUserService.getCurrentUserOrNull();
        Map<String, Boolean> response = new HashMap<>();
        boolean subscribed = user != null && Boolean.TRUE.equals(user.getIsSubscribed());
        if (user != null && user.getUserId() != null) {
            try {
                Map<String, Object> status = billingSupportServiceClient.subscriptionStatus(user.getUserId());
                Object active = status.get("active");
                subscribed = Boolean.TRUE.equals(active) || "true".equalsIgnoreCase(String.valueOf(active));
            } catch (Exception ignored) {
            }
        }
        response.put("subscribed", subscribed);
        return response;
    }

    @GetMapping("/subscriptionForm")
    public String subscribe(HttpSession session) {
        session.setAttribute("tagetUrl", "/landingPage");
        return "subscriptionForm";
    }

    @GetMapping("/checkSubscribe")
    public String redirectPayment(HttpSession session, Model model, @RequestParam("propertyId") Long propertyId) {
        session.setAttribute("tagetUrl", "/getOwnerDetails");
        session.setAttribute("propertyId", propertyId);
        User user = currentUserService.getCurrentUserOrNull();
        if (user != null && user.getUserId() != null) {
            try {
                Map<String, Object> status = billingSupportServiceClient.subscriptionStatus(user.getUserId());
                Object active = status.get("active");
                boolean subscribed = Boolean.TRUE.equals(active) || "true".equalsIgnoreCase(String.valueOf(active));
                if (subscribed) {
                    return "redirect:/getOwnerDetails";
                }
            } catch (Exception ignored) {
            }
        }
        return "subscriptionForm";
    }

    @GetMapping("/getOwnerDetails")
    public String getOwnerDetails(HttpSession session, Model model) {
        try {
            Object propertyIdObj = session.getAttribute("propertyId");
            if (propertyIdObj instanceof Long propertyId) {
                var property = propertyServiceClient.getPropertyById(propertyId);
                Long ownerId = property != null ? property.getOwnerId() : null;
                if (ownerId != null) {
                    var owner = authServiceClient.getUserById(ownerId);
                    model.addAttribute("owner", owner);
                }
            }
        } catch (Exception ignored) {
        }
        return "success";
    }

    @GetMapping("/getSubscriptionForm")
    public String getSubscriptionPage() {
        return "subscriptionForm";
    }
}
