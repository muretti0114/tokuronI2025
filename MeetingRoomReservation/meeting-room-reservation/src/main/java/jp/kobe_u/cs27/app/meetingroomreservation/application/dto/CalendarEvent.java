package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import lombok.Data;

/**
 * FullCalendar用のイベントクラス．予約DTOから変換して生成する
 */
@Data
public class CalendarEvent {
    Date start;
    Date end;
    String title;

    /* 予約DTOから生成する */
    public static CalendarEvent create(ReservationDto r) {
        CalendarEvent e = new CalendarEvent();
        String dateStr = r.getDate().toString();
        String startStr = dateStr + 'T' + r.getStartTime().toString();
        String endStr = dateStr + 'T' + r.getEndTime().toString();
        System.err.println("START:" + startStr);
        System.err.println("END:" + endStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

        try {
            e.setStart(sdf.parse(startStr));
            e.setEnd(sdf.parse(endStr));
            //部屋番号と目的をイベント・タイトルにする
            e.setTitle(r.getRoomNumber() + ":" + r.getPurpose());
        } catch (ParseException ex) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE, ex.getMessage());
        }
        return e;
    }

}