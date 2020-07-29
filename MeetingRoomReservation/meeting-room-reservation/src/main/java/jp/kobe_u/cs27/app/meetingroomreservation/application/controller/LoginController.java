package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
  	/**
	 * ログイン画面 に遷移する。
	 */
	@RequestMapping("/login")
	public String showLoginForm(Model model) {

		//ログイン画面。
		return "login";
	}

}