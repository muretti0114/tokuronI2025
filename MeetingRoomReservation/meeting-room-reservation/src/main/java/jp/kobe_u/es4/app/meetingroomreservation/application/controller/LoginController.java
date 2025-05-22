package jp.kobe_u.es4.app.meetingroomreservation.application.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	/* -------------------------- UC1: ログインする    ----------------------- */

	/**
	 * ログイン画面を表示する．（具体的なログイン処理は，Spring Securityに任せる）
	 */
	@RequestMapping("/login")
	public String showLoginForm(@RequestParam Map<String, String> params ,Model model) {
		//パラメータ処理．ログアウト時は?logout, 認証失敗時は?errorが帰ってくる（WebSecurityConfig.java参照） 
		if (params.containsKey("logout")) {
			model.addAttribute("message", "ログアウトしました");
		} else if (params.containsKey("error")) {
			model.addAttribute("message", "ログインに失敗しました");
		} 

		//ログイン画面。
		return "login";
	}

	@RequestMapping("/")
	public String redirectToMainPage() {
		return "redirect:/reservations";
	}



}