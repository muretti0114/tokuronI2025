package jp.kobe_u.es4.app.meetingroomreservation.domain.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会議室エンティティ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long rid; //会議室ID
    
    @NotBlank
    @Size(max=16)
    String roomNumber; //部屋番号

    @Size(max=64)
    String name; //会議室名

    @Size(max=64)
    String building;  //建物名

    @Size(max=512)
    String description; //説明

    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt; //作成日時

    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt; //更新日時
}