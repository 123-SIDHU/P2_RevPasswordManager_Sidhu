package com.rev.app.controller;

import com.rev.app.dto.ChangePasswordDTO;
import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.repository.ISecurityQuestionRepository;
import com.rev.app.service.ISecurityAuditService;
import com.rev.app.service.IUserService;
import com.rev.app.service.IVerificationService;
import com.rev.app.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/security")
public class SecurityController {

    @Value("${app.audit.old-password-days:90}")
    private int oldPasswordDays;

    private final IUserService userService;
    private final ISecurityAuditService auditService;
    private final IVerificationService verificationService;
    private final ISecurityQuestionRepository sqRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;

    public SecurityController(IUserService userService,
            ISecurityAuditService auditService,
            IVerificationService verificationService,
            ISecurityQuestionRepository sqRepository,
            PasswordEncoder passwordEncoder,
            AuthUtil authUtil) {
        this.userService = userService;
        this.auditService = auditService;
        this.verificationService = verificationService;
        this.sqRepository = sqRepository;
        this.passwordEncoder = passwordEncoder;
        this.authUtil = authUtil;
    }

    // ===== Security Audit =====
    @GetMapping("/audit")
    public String audit(Model model) {
        User user = authUtil.getCurrentUser();
        ISecurityAuditService.AuditReport report = auditService.generateReport(user.getId(), oldPasswordDays);
        model.addAttribute("report", report);
        model.addAttribute("user", user);
        return "security/audit";
    }

    // ===== Change Master Password =====
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        model.addAttribute("user", authUtil.getCurrentUser());
        return "security/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttrs,
            Model model) {
        User user = authUtil.getCurrentUser();
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "security/change-password";
        }
        try {
            userService.changeMasterPassword(user.getId(), dto);
            redirectAttrs.addFlashAttribute("successMsg", "Master password changed successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("user", user);
            return "security/change-password";
        }
    }

    // ===== 2FA (Email OTP) =====

    @GetMapping("/2fa")
    public String twoFactorPage(Model model) {
        User user = authUtil.getCurrentUser();
        model.addAttribute("user", user);
        return "security/2fa-setup";
    }

    /** Enable 2FA: send OTP to email, redirect to verify page */
    @PostMapping("/2fa/enable")
    public String enable2FA(RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        // Generate and email the OTP so user can confirm
        verificationService.generateAndSendOtp(user, "2FA");
        redirectAttrs.addFlashAttribute("infoMsg",
                "A verification code has been sent to " + user.getEmail() + ". Enter it below to enable 2FA.");
        return "redirect:/security/2fa/verify";
    }

    @GetMapping("/2fa/verify")
    public String twoFaVerifyPage(Model model) {
        model.addAttribute("user", authUtil.getCurrentUser());
        return "security/2fa-verify";
    }

    @PostMapping("/2fa/verify")
    public String confirmEnable2FA(@RequestParam String otp, RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        boolean valid = verificationService.validateCode(user, otp, "2FA");
        if (!valid) {
            redirectAttrs.addFlashAttribute("errorMsg", "Invalid or expired code. Please try again.");
            return "redirect:/security/2fa/verify";
        }
        userService.toggle2FA(user.getId(), true);
        redirectAttrs.addFlashAttribute("successMsg",
                "Two-factor authentication enabled! You will receive an email OTP on each login.");
        return "redirect:/security/2fa";
    }

    @PostMapping("/2fa/disable")
    public String disable2FA(@RequestParam String masterPassword, RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        if (!userService.verifyMasterPassword(user, masterPassword)) {
            redirectAttrs.addFlashAttribute("errorMsg", "Incorrect master password");
            return "redirect:/security/2fa";
        }
        userService.toggle2FA(user.getId(), false);
        redirectAttrs.addFlashAttribute("successMsg", "Two-factor authentication disabled.");
        return "redirect:/security/2fa";
    }

    // ===== Security Questions =====
    @GetMapping("/questions")
    public String questionsPage(Model model) {
        User user = authUtil.getCurrentUser();
        List<SecurityQuestion> questions = sqRepository.findByUserId(user.getId());
        model.addAttribute("questions", questions);
        model.addAttribute("user", user);
        return "security/questions";
    }

    @PostMapping("/questions/update")
    @org.springframework.transaction.annotation.Transactional
    public String updateQuestions(@RequestParam String masterPassword,
            @RequestParam List<String> questionTexts,
            @RequestParam List<String> answers,
            RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        if (user == null)
            return "redirect:/login";

        if (!userService.verifyMasterPassword(user, masterPassword)) {
            redirectAttrs.addFlashAttribute("errorMsg", "Incorrect master password");
            return "redirect:/security/questions";
        }

        List<SecurityQuestion> newQuestions = new ArrayList<>();
        int maxSize = Math.min(questionTexts.size(), answers.size());
        for (int i = 0; i < maxSize; i++) {
            String qText = questionTexts.get(i);
            String ans = answers.get(i);
            if (qText != null && !qText.isBlank() && ans != null && !ans.isBlank()) {
                newQuestions.add(SecurityQuestion.builder()
                        .user(user)
                        .questionText(qText.trim())
                        .answerHash(passwordEncoder.encode(ans.toLowerCase().trim()))
                        .build());
            }
        }

        if (newQuestions.size() < 3) {
            redirectAttrs.addFlashAttribute("errorMsg", "Minimum 3 valid security questions and answers are required");
            return "redirect:/security/questions";
        }

        // Delete only after we know we have valid new questions to replace them with
        sqRepository.deleteByUserId(user.getId());
        sqRepository.saveAll(newQuestions);

        redirectAttrs.addFlashAttribute("successMsg", "Security questions updated successfully!");
        return "redirect:/security/questions";
    }
}
