package com.no_broker_application.Web_UI.controller;

import com.no_broker_application.Web_UI.client.PropertyServiceClient;
import com.no_broker_application.Web_UI.client.AuthServiceClient;
import com.no_broker_application.Web_UI.client.BillingSupportServiceClient;
import com.no_broker_application.Web_UI.dto.UpdateUserRequest;
import com.no_broker_application.Web_UI.dto.User;
import com.no_broker_application.Web_UI.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final PropertyServiceClient propertyServiceClient;
    private final AuthServiceClient authServiceClient;
    private final BillingSupportServiceClient billingSupportServiceClient;
    private final CurrentUserService currentUserService;

    @GetMapping("/profile/view/{userId}")
    public String viewProfile(@PathVariable("userId") Long userId, HttpSession session, Model model) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null || !user.getUserId().equals(userId)) {
            return "redirect:/landingPage";
        }
        model.addAttribute("user", user);
        return "edit-profile-details";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, HttpSession session) {
        if (updatedUser == null || updatedUser.getUserId() == null) {
            return "redirect:/landingPage";
        }
        User current = currentUserService.getCurrentUserOrNull();
        if (current == null || current.getUserId() == null || !current.getUserId().equals(updatedUser.getUserId())) {
            return "redirect:/landingPage";
        }

        UpdateUserRequest req = new UpdateUserRequest();
        req.setName(updatedUser.getName());
        req.setMobilePhone(updatedUser.getMobilePhone());

        try {
            authServiceClient.updateUser(updatedUser.getUserId(), req);
        } catch (Exception ignored) {
        }

        return "redirect:/profile/view/" + updatedUser.getUserId();
    }

    @GetMapping("/shortlisted-properties/{userId}")
    public String showShortlisted(@PathVariable("userId") Long userId, HttpSession session, Model model) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null || !user.getUserId().equals(userId)) {
            return "redirect:/landingPage";
        }
        model.addAttribute("user", user);
        try {
            var properties = propertyServiceClient.getBookmarkedProperties(userId);
            model.addAttribute("allProperties", properties);
        } catch (Exception e) {
            model.addAttribute("allProperties", List.of());
            model.addAttribute("error", "Could not load shortlisted properties");
        }
        return "shortlist";
    }

    @GetMapping("/shortlisted-payments/{userId}")
    public String showPayments(@PathVariable("userId") Long userId, HttpSession session, Model model) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null || !user.getUserId().equals(userId)) {
            return "redirect:/landingPage";
        }
        model.addAttribute("user", user);
        try {
            var tx = billingSupportServiceClient.listTransactions(userId);
            model.addAttribute("transactions", tx);
        } catch (Exception e) {
            model.addAttribute("transactions", List.of());
            model.addAttribute("error", "Could not load transactions");
        }
        return "payments";
    }

    @GetMapping("/your-properties/{userId}")
    public String showUserProperties(@PathVariable("userId") Long userId, Model model, HttpSession session) {
        User user = currentUserService.getCurrentUserOrNull();
        if (user == null || user.getUserId() == null || !user.getUserId().equals(userId)) {
            return "redirect:/landingPage";
        }
        model.addAttribute("user", user);
        try {
            var properties = propertyServiceClient.getPropertiesByOwner(userId);
            model.addAttribute("properties", properties);
        } catch (Exception e) {
            model.addAttribute("properties", List.of());
            model.addAttribute("error", "Could not load properties");
        }
        return "your-properties";
    }

}
