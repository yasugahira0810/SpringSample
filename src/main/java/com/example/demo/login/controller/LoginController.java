package com.example.demo.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    /**
     * ログイン画面のGETメソッド用処理.
     */
    @GetMapping({ "/", "/login" })
    public String getLogin(Model model) {

        // login.htmlに画面遷移
        return "login/login";
    }

    /**
     * ログイン画面のPOSTメソッド用処理.
     */
    @PostMapping("/login")
    public String postLogin(Model model) {

        System.out.println("##########   postLogin Method executed   ##########");
        // login.htmlに画面遷移
        return "login/login";
    }

    /**
     * ログイン画面のPOSTメソッド用処理.
     */
    @GetMapping("/ababa")
    public String postAbaba(Model model) {

        System.out.println("##########   postLogin Method executed   ##########");
        // login.htmlに画面遷移
        return "ababa/ababa";
    }
}