package jp.kobe_u.es4.app.meetingroomreservation.domain.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;

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