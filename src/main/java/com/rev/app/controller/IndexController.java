package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final AuthUtil authUtil;

    public IndexController(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        User user = authUtil.getCurrentUser();
        model.addAttribute("user", user);
        return "index";
    }
}
