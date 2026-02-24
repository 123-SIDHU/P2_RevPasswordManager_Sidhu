package com.rev.app.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rev.app.entity.Credential;
import com.rev.app.entity.SecurityQuestion;
import com.rev.app.entity.User;
import com.rev.app.service.OTPService;
import com.rev.app.service.PasswordManagerService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private PasswordManagerService service;

    @Autowired
    private OTPService otpService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "index";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = service.login(username, password);
        if (user != null) {
            if (user.isTwoFactorEnabled()) {
                session.setAttribute("pendingUser", user);
                String otp = otpService.generateOTP(user.getId(), "LOGIN");
                System.out.println("DEBUG OTP for " + user.getUsername() + ": " + otp);
                return "redirect:/otp";
            }
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid username or password");
        return "index";
    }

    @GetMapping("/otp")
    public String otpPage() {
        return "otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        User pendingUser = (User) session.getAttribute("pendingUser");
        if (pendingUser == null) return "redirect:/login";

        if (otpService.validateOTP(pendingUser.getId(), otp, "LOGIN")) {
            session.removeAttribute("pendingUser");
            session.setAttribute("user", pendingUser);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid or Expired OTP");
            return "otp";
        }
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String doUpdateProfile(@ModelAttribute User updatedUser, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        updatedUser.setId(user.getId());
        service.updateProfile(updatedUser);

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhoneNumber(updatedUser.getPhoneNumber());

        model.addAttribute("msg", "Profile Updated Successfully");
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/2fa")
    public String update2FA(@RequestParam boolean enable, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        service.toggle2FA(user.getId(), enable);
        user.setTwoFactorEnabled(enable);

        model.addAttribute("user", user);
        model.addAttribute("msg", "2FA Settings Updated");
        return "profile";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user,
                             @RequestParam String confirmPassword,
                             @RequestParam String q1, @RequestParam String a1,
                             @RequestParam String q2, @RequestParam String a2,
                             @RequestParam String q3, @RequestParam String a3,
                             Model model) {
        if (!user.getMasterPasswordHash().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        List<SecurityQuestion> questions = new ArrayList<>();
        questions.add(new SecurityQuestion(0, q1, a1));
        questions.add(new SecurityQuestion(0, q2, a2));
        questions.add(new SecurityQuestion(0, q3, a3));

        try {
            service.registerUser(user, questions);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false) String category) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Credential> credentials;
        if (search != null && !search.isEmpty()) {
            credentials = service.searchCredentials(user.getId(), search);
        } else {
            credentials = service.getCredentials(user.getId(), sort, category);
        }

        model.addAttribute("credentials", credentials);
        model.addAttribute("user", user);
        model.addAttribute("totalPasswords", service.getTotalPasswords(user.getId()));
        model.addAttribute("favoriteCount", service.getFavoriteCount(user.getId()));
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedCategory", category);

        return "dashboard";
    }

    @PostMapping("/dashboard/favorite/{id}")
    @ResponseBody
    public void toggleFavorite(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("user") == null) return;
        service.toggleFavorite(id);
    }

    @GetMapping("/dashboard/add")
    public String addCredentialShort(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        return "add-credential";
    }

    @PostMapping("/dashboard/add")
    public String doAddCredential(@ModelAttribute Credential credential, @RequestParam String masterPassword, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        User freshUser = service.login(user.getUsername(), masterPassword);
        if (freshUser == null) {
            model.addAttribute("error", "Invalid Master Password");
            return "add-credential";
        }

        credential.setUserId(user.getId());
        try {
            service.addCredential(credential, masterPassword);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding credential");
            return "add-credential";
        }
    }

    @GetMapping("/dashboard/view/{id}")
    public String viewCredential(@PathVariable int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("credentialId", id);
        return "view-credential";
    }

    @PostMapping("/dashboard/view/{id}")
    public String doViewCredential(@PathVariable int id, @RequestParam String masterPassword, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            Credential credential = service.getCredential(id, masterPassword);
            model.addAttribute("credential", credential);
            model.addAttribute("masterPassword", masterPassword);
            return "view-credential";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid Master Password");
            model.addAttribute("credentialId", id);
            return "view-credential";
        }
    }

    @PostMapping("/dashboard/update")
    public String doUpdateCredential(@ModelAttribute Credential credential, @RequestParam String masterPassword, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            service.updateCredential(credential, masterPassword);
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Update failed");
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/dashboard/delete/{id}")
    public String deleteCredential(@PathVariable int id, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        service.deleteCredential(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/generate")
    public String generatorPage(HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        return "generator";
    }

    @PostMapping("/generate")
    @ResponseBody
    public List<String> generatePassword(@RequestParam int length,
                                         @RequestParam(defaultValue="false") boolean upper,
                                         @RequestParam(defaultValue="false") boolean lower,
                                         @RequestParam(defaultValue="false") boolean digits,
                                         @RequestParam(defaultValue="false") boolean special,
                                         @RequestParam(defaultValue="false") boolean excludeSimilar,
                                         @RequestParam(defaultValue="1") int count) {
        return service.generatePasswords(count, length, upper, lower, digits, special, excludeSimilar);
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String doForgotPassword(@RequestParam String username, Model model, HttpSession session) {
        java.util.Optional<User> userOpt = service.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<SecurityQuestion> questions = service.getSecurityQuestions(user.getId());
            session.setAttribute("resetUser", user);
            model.addAttribute("questions", questions);
            return "reset-password";
        }
        model.addAttribute("error", "Username not found");
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String doResetPassword(@RequestParam String a1, @RequestParam String a2, @RequestParam String a3,
                                  @RequestParam String newPassword, HttpSession session, Model model) {
        User user = (User) session.getAttribute("resetUser");
        if (user == null) return "redirect:/login";

        List<SecurityQuestion> questions = service.getSecurityQuestions(user.getId());
        if (com.rev.app.util.CryptoUtil.verifyPassword(a1, questions.get(0).getAnswerHash()) &&
                com.rev.app.util.CryptoUtil.verifyPassword(a2, questions.get(1).getAnswerHash()) &&
                com.rev.app.util.CryptoUtil.verifyPassword(a3, questions.get(2).getAnswerHash())) {

            service.resetMasterPassword(user.getUsername(), newPassword);
            session.removeAttribute("resetUser");
            return "redirect:/login?msg=ResetSuccessful";
        }
        model.addAttribute("error", "Incorrect security answers");
        model.addAttribute("questions", questions);
        return "reset-password";
    }

    @GetMapping("/audit")
    public String audit(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("report", service.auditPasswords(user.getId()));
        return "audit";
    }

    @GetMapping("/export")
    @ResponseBody
    public ResponseEntity<String> exportVault(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).build();

        String json = service.exportVault(user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vault_export.json")
                .body(json);
    }

    @PostMapping("/import")
    public String importVault(@RequestParam String json, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        service.importVault(user.getId(), json);
        return "redirect:/dashboard";
    }
}
