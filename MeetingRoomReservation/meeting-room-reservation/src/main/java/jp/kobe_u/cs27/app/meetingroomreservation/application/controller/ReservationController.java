package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.UserService;

@Controller
public class ReservationController {
    @Autowired
    UserService us;
    
    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(name="mon", required=false) String yyyymm, Model model,
    Principal principal) {
        int year, month;
        if (yyyymm == null) { //年月指定がない場合は，本日の年月．
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
        } else { //年月指定がある場合はチェック
            Pattern p = Pattern.compile("(\\d\\d\\d\\d)(\\d\\d)");
            Matcher m = p.matcher(yyyymm);
            if (m.find()) {
                String matchstr = m.group();
                System.out.println(matchstr + "の部分にマッチしました");
                year = Integer.parseInt(m.group(1));
                month = Integer.parseInt(m.group(2));
                if (month<1 || month>12) {
                    throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_MONTH,
                    yyyymm + ": Invalid reservation month");
                }
            } else {
                throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_MONTH,
                yyyymm + ": Invalid reservation month");
            }
       }
       Authentication auth = (Authentication)principal;
        UserSession userSession = (UserSession)auth.getPrincipal();
       model.addAttribute("year", year);
       model.addAttribute("month", month);
       model.addAttribute("user", userSession);

       return "reservation/calendar";
    }

}