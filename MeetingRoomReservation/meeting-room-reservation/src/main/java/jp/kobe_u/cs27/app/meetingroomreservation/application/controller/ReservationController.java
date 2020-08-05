package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import jp.kobe_u.cs27.app.meetingroomreservation.application.utils.YoyakuUtils;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Reservation;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.ReservationService;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.RoomService;

@Controller
public class ReservationController {
    @Autowired
    ReservationService rsvService;
    @Autowired
    RoomService roomService;

    /*--------------------------- UC3: カレンダーを見る ------------------------------*/
    /**
     * 予約カレンダーを表示する
     */
    @GetMapping("/reservations")
    public String showCalendar(@RequestParam(name = "mon", required = false) String yyyymm, Model model) {
        // 年月文字列を検査し，当月1日を取得
        Date date = YoyakuUtils.validateMonth(yyyymm);
        model.addAttribute("initialDate", date);

        // ログインユーザの情報を取得
        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);

        // そのユーザの全予約をサービスから取得
        List<ReservationDto> rsvList = rsvService.getMyCalendar(user.getUsername());

        // FullCalendar用のイベントクラスに詰め替え
        ArrayList<CalendarEvent> events = new ArrayList<>();
        for (ReservationDto r : rsvList) {
            events.add(CalendarEvent.create(r));
        }
        // モデルにセット
        model.addAttribute("events", events);
        // カレンダーを表示
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
        // 日付文字列を検査して，日付オブジェクトを取得
        Date date = YoyakuUtils.validateDate(yyyymmdd);
        model.addAttribute("date", date);

        // その日の全予約をサービスから取得
        List<ReservationDto> rsvList = rsvService.getReservationsByDate(date);
        rsvList.sort((a,b) -> (a.getRid() > b.getRid())? 1:-1); //部屋順で並べ替えておく（表示上の都合）

        // 全部屋を取得
        List<Room> roomList = roomService.getAllRooms();
        // その日の全予約を部屋ごとに仕分けて画面にセット
        Map<String, List<ReservationDto>> rsvListByRoom = YoyakuUtils.divideReservationsIntoRooms(rsvList);
        model.addAttribute("rsvListByRoom", rsvListByRoom);

        // GoogleチャートのTimeline用のデータを生成する
        ArrayList<String[]> chartData = new ArrayList<>();
        for (Room room : roomList) { // すべての部屋について，
            String rNum = room.getRoomNumber();
            // 開館・閉館時間を設定
            String[] begin = { rNum, "開館", yyyymmdd + "T" + "06:00:00", yyyymmdd + "T" + "06:00:00" };
            String[] end = { rNum, "閉館", yyyymmdd + "T" + "21:00:00", yyyymmdd + "T" + "21:00:00" };
            chartData.add(begin);
            chartData.add(end);
   
            // 各部屋の予約状況をセット
            if (rsvListByRoom.containsKey(rNum)) {
                for (ReservationDto r : rsvListByRoom.get(rNum)) {
                    String[] item = new String[4];
                    item[0] = rNum; //部屋名
                    item[1] = r.getUser().getName() + " | " + r.getPurpose(); //イベント名
                    item[2] = r.getDate().toString() + "T" + r.getStartTime().toString(); //開始時刻
                    item[3] = r.getDate().toString() + "T" + r.getEndTime().toString(); //終了時刻
                    chartData.add(item);
                }
            }
        }
        //画面にセット
        model.addAttribute("chartData", chartData);

        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);

        return "reservation/reservation";
    }

    /*--------------------------- UC4: 予約を作成する ------------------------------*/
    /**
     * 予約作成フォームを表示する
     * 
     * @param yyyymmdd
     * @param model
     * @return
     */
    @GetMapping("/reservations/{yyyymmdd}/create")
    String showCreateForm(@PathVariable String yyyymmdd, Model model) {

        // ユーザ情報をセット
        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);
        // 全会議室の情報をセット
        List<Room> roomList = roomService.getAllRooms();
        model.addAttribute("roomList", roomList);

        // ブランクのフォームにユーザ名と日付をセット
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
        UserSession user = YoyakuUtils.getUserSession();
        if (result.hasErrors()) {
            List<Room> roomList = roomService.getAllRooms();
            model.addAttribute("roomList", roomList);
            model.addAttribute("user", user);
            return "reservation/create";
        }
        // 開始・終了時刻のチェック
        if (form.getStartTime().compareTo(form.getEndTime()) >= 0) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                    "startTime must be before endTime");
        }
        // 空きチェック
        Reservation r = form.toEntity();
        if (rsvService.isVacant(r.getRid(), r.getDate(), r.getStartTime(), r.getEndTime()) == false) {
            Room room = roomService.getRoom(r.getRid());
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_BOOKED, "Room " + room.getRoomNumber()
                    + " is already booked at " + r.getStartTime() + " to " + r.getEndTime());
        }

        // 画面にモデルをセットして
        model.addAttribute("user", user);
        model.addAttribute("roomNumber", roomService.getRoom(form.getRid()).getRoomNumber());
        model.addAttribute("reservationForm", form);
        // チェック画面を表示
        return "reservation/check";
    }

    /**
     * 予約を登録する
     * 
     * @param form
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/reservations")
    String registerReservation(@ModelAttribute @Validated ReservationForm form, BindingResult result, Model model) {
        rsvService.add(form.toEntity());

        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);
        model.addAttribute("date", form.getDate());
        return "reservation/created";
    }

    /*--------------------------- UC5: 予約を変更する ------------------------------*/
    /**
     * 変更画面を表示する
     * 
     * @return
     */
    @GetMapping("/reservations/{number}/change")
    String showChangeForm(@PathVariable Long number, Model model) {
        ReservationDto r = rsvService.getReservationDto(number);

        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("reservation", r.toForm());
        model.addAttribute("roomNumber", r.getRoom().getRoomNumber());
        model.addAttribute("user", user);
        return "reservation/change";
    }

    /**
     * 実際に予約を変更する
     */
    @PostMapping("/reservations/change")
    String changeReservation(@ModelAttribute("reservation") ReservationForm form, Model model) {

        // 開始・終了時刻のチェック
        if (form.getStartTime().compareTo(form.getEndTime()) >= 0) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                    "startTime must be before endTime");
        }

        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);
        // とりあえずフォームからエンティティを作る
        Reservation r = form.toEntity();
        // 更新する
        rsvService.change(user.getUsername(), r.getNumber(), r.getStartTime(), r.getEndTime());

        model.addAttribute("date", form.getDate());

        return "reservation/changed";
    }

    /*--------------------------- UC6: 予約をキャンセルする ------------------------------*/
    /**
     * キャンセル画面を表示する
     * 
     * @return
     */
    @GetMapping("/reservations/{number}/cancel")
    String showCancelForm(@PathVariable Long number, Model model) {
        UserSession user = YoyakuUtils.getUserSession();
        ReservationDto r = rsvService.getReservationDto(number);
        model.addAttribute("reservation", r.toForm());
        model.addAttribute("roomNumber", r.getRoom().getRoomNumber());
        model.addAttribute("user", user);
        return "reservation/cancel";
    }

    /**
     * 実際に予約をキャンセルする
     */
    @PostMapping("/reservations/delete")
    String cancelReservation(@ModelAttribute("reservation") ReservationForm form, Model model) {
        UserSession user = YoyakuUtils.getUserSession();
        model.addAttribute("user", user);
        rsvService.cancel(user.getUsername(), form.getNumber());

        model.addAttribute("date", form.getDate());

        return "reservation/canceled";
    }

}