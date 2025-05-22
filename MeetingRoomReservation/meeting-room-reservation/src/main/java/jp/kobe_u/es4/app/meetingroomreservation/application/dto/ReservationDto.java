package jp.kobe_u.es4.app.meetingroomreservation.application.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.Reservation;
import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDto {
    Long number;
    Long rid; // 会議室ID
    RoomDto room; // 部屋番号
    String uid; // ユーザID
    UserDto user; // ユーザ名
    Date date; // 予約日
    Date startTime; // 開始時刻
    Date endTime; // 終了時刻
    String purpose; // 用途

    /**
     * 予約DTOを作成する
     * @param resv
     * @param room
     * @param user
     * @return
     */
    public static ReservationDto build(Reservation resv, Room room, User user) {
        ReservationDto dto = new ReservationDto(resv.getNumber(), resv.getRid(), RoomDto.build(room), resv.getUid(),
                UserDto.build(user), resv.getDate(), resv.getStartTime(), resv.getEndTime(), resv.getPurpose());
        return dto;
    }

    /**
     * 予約フォームに変換する
     */
    public ReservationForm toForm() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            ReservationForm f = new ReservationForm(getNumber(), getRid(), getUid(), getDate().toString(),
                    sdf.format(getStartTime()), sdf.format(getEndTime()), getPurpose());
        return f;
    }

}