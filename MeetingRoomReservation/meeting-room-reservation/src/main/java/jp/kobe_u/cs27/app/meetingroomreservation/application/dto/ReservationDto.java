package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import java.util.Date;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDto {
    Long number;
    Long rid;   //会議室ID
    String roomNumber;  //部屋番号
    String uid; //ユーザID
    String userName;  //ユーザ名
    Date date;  //予約日
    Date startTime;  //開始時刻
    Date endTime;    //終了時刻
    String purpose; //用途

    public static ReservationDto build(Reservation resv, String roomNumber, String userName) {
        ReservationDto dto = new ReservationDto(
            resv.getNumber(),
            resv.getRid(),
            roomNumber,
            resv.getUid(),
            userName,
            resv.getDate(),
            resv.getStartTime(),
            resv.getEndTime(),
            resv.getPurpose()
        );

        return dto;
    }
}