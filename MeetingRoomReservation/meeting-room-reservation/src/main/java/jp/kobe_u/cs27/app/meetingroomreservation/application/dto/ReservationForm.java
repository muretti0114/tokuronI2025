package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Reservation;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationForm {
    @NotNull
    Long rid;

    @NotBlank
    String uid;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    String date;

    @NotBlank
    @Pattern(regexp = "\\d{2}:\\d{2}")
    String startTime;

    @NotBlank
    @Pattern(regexp = "\\d{2}:\\d{2}")
    String endTime;

    @NotBlank
    String purpose;

    public Reservation toEntity() {
        Reservation r = new Reservation();
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            r.setRid(rid);
            r.setUid(uid);
            r.setDate(datef.parse(date));
            String start = date + "T" + startTime + ":00";
            String end = date + "T" + endTime + ":00";
            r.setStartTime(timef.parse(start));
            r.setEndTime(timef.parse(end));
            r.setPurpose(purpose);
        } catch (ParseException e) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_ROOM_FORM, e.getMessage());
        }
        return r;
    
    }

}