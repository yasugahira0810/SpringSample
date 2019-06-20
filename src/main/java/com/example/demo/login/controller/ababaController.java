package com.example.demo.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ababaController {

    /**
     * ログイン画面のGETメソッド用処理.
     */
    @GetMapping("/abababa")
    public String getLogin(Model model) {

        // login.htmlに画面遷移
        return "abababa/abababa";
    }

    @GetMapping("/abababa/abe")
    public String getabe(Model model) {

        // login.htmlに画面遷移
        return "abababa/abe";
    }
}