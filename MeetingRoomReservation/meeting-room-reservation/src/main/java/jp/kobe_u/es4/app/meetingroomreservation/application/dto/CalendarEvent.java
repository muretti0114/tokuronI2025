package jp.kobe_u.es4.app.meetingroomreservation.application.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.kobe_u.es4.app.meetingroomreservation.domain.exception.YoyakuAppException;
import lombok.Data;

/**
 * FullCalendar用のイベントクラス．予約DTOから変換して生成する
 */
@Data
public class CalendarEvent {
    Date start;
    Date end;
    String title;

    /* 予約DTOからFullCalebdar用のイベントを生成する */
    public static CalendarEvent create(ReservationDto r) {
        CalendarEvent e = new CalendarEvent();
        String dateStr = r.getDate().toString();
        String startStr = dateStr + 'T' + r.getStartTime().toString();
        String endStr = dateStr + 'T' + r.getEndTime().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");

        try {
            e.setStart(sdf.parse(startStr));
            e.setEnd(sdf.parse(endStr));
            //目的をイベント・タイトルにする
            e.setTitle(r.getPurpose());
        } catch (ParseException ex) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE, ex.getMessage());
        }
        return e;
    }

}