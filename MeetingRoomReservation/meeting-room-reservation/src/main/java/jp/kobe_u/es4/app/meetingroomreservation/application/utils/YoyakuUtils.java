package jp.kobe_u.es4.app.meetingroomreservation.application.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jp.kobe_u.es4.app.meetingroomreservation.application.dto.ReservationDto;
import jp.kobe_u.es4.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.es4.app.meetingroomreservation.domain.exception.YoyakuAppException;

/**
 * 会議室予約アプリののユーティリティ
 */
public class YoyakuUtils {
    /**
     * ユーティリティ関数．ユーザセッションを取得する
     */
    public static UserSession getUserSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserSession userSession = (UserSession) auth.getPrincipal();
        return userSession;
    }

    /**
     * 年月日文字列を検査し，Dateオブジェクトを返す
     * @param yyyymmdd
     * @return 
     */
    public static Date validateDate(String yyyymmdd) {
        Date date = null;
        // 日付のチェック
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (yyyymmdd == null || yyyymmdd.length() != 10) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                    yyyymmdd + ": Invalid date, which must be yyyy-mm-dd");
        } else {
            sdf.setLenient(false);
            try {
                date = sdf.parse(yyyymmdd);
            } catch (ParseException e) {
                throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                        yyyymmdd + ": Invalid date, which is non-existent");
            }
        }
        return date;
    }

    /**
     * 年月文字列をチェックしDateオブジェクトを返す
     */
    public static Date validateMonth(String yyyymm) {
        if (yyyymm==null) {
            //nullの場合は当月を取得
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            yyyymm = String.format("%04d-%02d", year, month);
        } 
        //7桁かどうかチェック
        if (yyyymm.length()==7) {
            yyyymm = yyyymm + "-01"; //当月の1日をバリデート
        } else if (yyyymm.length()!=10) { //すでにyyyy-mm-dd から外れている??
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
            yyyymm + ": Invalid month, which is non-existent");
        }

        return validateDate(yyyymm);

    }

    public static Map<String, List<ReservationDto>> divideReservationsIntoRooms(
        List<ReservationDto> reservations) {
        LinkedHashMap <String, List<ReservationDto>> map;
        map = new LinkedHashMap<String, List<ReservationDto>>();
        
        for (ReservationDto r: reservations) {
            String room = r.getRoom().getRoomNumber();
            if (map.containsKey(room)) {
                map.get(room).add(r);
            } else {
                ArrayList<ReservationDto>list = new ArrayList<>();
                list.add(r);
                map.put(room, list);
            }
        }

        return map;
    }  

}