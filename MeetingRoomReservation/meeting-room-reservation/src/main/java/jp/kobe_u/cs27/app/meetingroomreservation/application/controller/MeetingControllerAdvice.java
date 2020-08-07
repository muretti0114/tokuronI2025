package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;

@ControllerAdvice
public class MeetingControllerAdvice {
    /**
     * 予約アプリのビジネス例外のハンドリング
     * @return カスタム・エラーページを表示
     */
    @ExceptionHandler(YoyakuAppException.class)
    public String handleYoyakuException(YoyakuAppException ex, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserSession user = (UserSession) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("error", ex);
        return "yerror";
    }
    /**
     * Spring バリデーション例外のハンドリング．ビジネス例外にラップしなおしてハンドラを呼び出す
     */
    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException ex, Model model) {
        //ビジネス例外でラップして，上のハンドラに投げる
        YoyakuAppException e = new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_FORM, ex.getMessage());
        return handleYoyakuException(e, model);
    }
    /**
     * その他，一般例外のハンドリング．ビジネス例外にラップしなおしてハンドラを呼び出す
     */
    @ExceptionHandler(Exception.class)
    public String handleBindException(Exception ex, Model model) {
        //ビジネス例外でラップして，上のハンドラに投げる
        YoyakuAppException e = new YoyakuAppException(YoyakuAppException.ERROR, ex.getMessage());
        return handleYoyakuException(e, model);
    }
}