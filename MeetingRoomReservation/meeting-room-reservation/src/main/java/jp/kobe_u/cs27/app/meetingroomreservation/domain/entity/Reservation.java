package jp.kobe_u.cs27.app.meetingroomreservation.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 予約エンティティ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long number; //予約番号
    @NonNull
    Long rid; //会議室ID
    @NonNull
    String uid; //ユーザID
    @Temporal(TemporalType.DATE)
    Date date;  //予約日
    @Temporal(TemporalType.TIME)
    Date startTime;  //開始時刻
    @Temporal(TemporalType.TIME)
    Date endTime;    //終了時刻
    @NotBlank
    String purpose; //用途
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt; //作成日時
    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt;  //更新日時
    @Temporal(TemporalType.TIMESTAMP)
    Date canceledAt; //キャンセル日時
}