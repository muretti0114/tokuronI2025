package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.CalendarEvent;
import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.ReservationDto;
import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.ReservationForm;
import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Reservation;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.ReservationService;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.RoomService;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.UserService;

@Controller
public class ReservationController {
    @Autowired
    UserService us;
    @Autowired
    ReservationService ress;
    @Autowired
    RoomService rs;

    /**
     * 予約カレンダーを表示する
     */
    @GetMapping("/reservations")
    public String showCalendar(@RequestParam(name = "mon", required = false) String yyyymm, Model model) {
        UserSession user = getUserSession();
        int year, month;
        if (yyyymm == null) { // 年月指定がない場合は，本日の年月．
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
        } else { // 年月指定がある場合はチェック
            Pattern p = Pattern.compile("(\\d\\d\\d\\d)-(\\d\\d)");
            Matcher m = p.matcher(yyyymm);
            if (m.find()) {
                String matchstr = m.group();
                System.out.println(matchstr + "の部分にマッチしました");
                year = Integer.parseInt(m.group(1));
                month = Integer.parseInt(m.group(2));
                if (month < 1 || month > 12) {
                    throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_MONTH,
                            yyyymm + ": Invalid reservation month");
                }
            } else {
                throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_MONTH,
                        yyyymm + ": Invalid reservation month, which must be yyyy-mm");
            }
        }
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("user", user);
        List<ReservationDto> rsvList = ress.getMyCalendar(user.getUsername());
        ArrayList<CalendarEvent> events = new ArrayList<>();
        for (ReservationDto r: rsvList) {
            events.add(CalendarEvent.create(r));
        }
        model.addAttribute("events", events);

        return "reservation/calendar";
    }

    /**
     * 予約状況画面を表示する
     * 
     * @param yyyymmdd
     * @return
     */
    @GetMapping("/reservations/{yyyymmdd}")
    String showResevationsOfDay(@PathVariable String yyyymmdd, Model model) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (yyyymmdd == null || yyyymmdd.length() != 10) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                    yyyymmdd + "Invalid date, which must be yyyy-mm-dd");
        } else {
            sdf.setLenient(false);
            try {
                date = sdf.parse(yyyymmdd);
            } catch (ParseException e) {
                throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                        yyyymmdd + "Invalid date, which is non-existent");
            }
        }
        List<ReservationDto> list = ress.getReservationsByDate(date);
        model.addAttribute("roomList", rs.getAllRooms());
        model.addAttribute("rsvList", list);
        UserSession user = getUserSession();
        model.addAttribute("user", user);
        model.addAttribute("date", date);
        model.addAttribute("dStr", yyyymmdd);

        return "reservation/reservation";
    }

    /**
     * 予約作成フォームを表示する
     * @param yyyymmdd
     * @param model
     * @return
     */
    @GetMapping("/reservations/{yyyymmdd}/create")
    String showCreateForm(@PathVariable String yyyymmdd, Model model) {

        //ユーザ情報をセット
        UserSession user = getUserSession();
        model.addAttribute("user", user);
        //全会議室の情報をセット
        List<Room> roomList = rs.getAllRooms();
        model.addAttribute("roomList", roomList);

        //ブランクのフォームにユーザ名と日付をセット
        ReservationForm form = new ReservationForm();
        form.setUid(user.getUsername());
        form.setDate(yyyymmdd);
        model.addAttribute("reservationForm", form);

        return "reservation/create";
    }

    /**
     * 予約の確認画面を表示する
     */
    @PostMapping("/reservations/check")
    String checkReservation(@ModelAttribute @Validated ReservationForm form, BindingResult result, Model model) {
        UserSession user = getUserSession();
        if (result.hasErrors()) {
            List<Room> roomList = rs.getAllRooms();
            model.addAttribute("roomList", roomList);
            model.addAttribute("user", user);
            return "reservation/create";
        }
        //空きチェック
        Reservation r = form.toEntity();
        if (ress.isVacant(r.getRid(), r.getDate(), r.getStartTime(), r.getEndTime())==false) {
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_BOOKED, 
            "Room " + r.getRid() + " is already booked at " + r.getStartTime() + 
            " to " + r.getEndTime());
        }

        //画面にモデルをセットして
        model.addAttribute("user", user);
        model.addAttribute("roomNumber", rs.getRoom(form.getRid()).getRoomNumber());
        model.addAttribute("reservationForm", form);
        //チェック画面を表示
        return "reservation/check";
    }

    /**
     * 予約を登録する
     * @param form
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/reservations")
    String registerReservation(@ModelAttribute @Validated ReservationForm form, 
    BindingResult result, Model model) {
        ress.add(form.toEntity());

        UserSession user = getUserSession();
        model.addAttribute("user", user);
        return "reservation/created";
    }

    /**
     * ユーティリティ関数．ユーザセッションを取得する
     */
    private UserSession getUserSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserSession userSession = (UserSession) auth.getPrincipal();
        return userSession;
    }

}