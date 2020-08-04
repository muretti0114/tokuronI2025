package jp.kobe_u.cs27.app.meetingroomreservation.domain.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザエンティティ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    String uid;  //ユーザID

    @NotBlank
    @Size(max=128)
    String password; //パスワード

    @NotBlank
    @Size(max=32)
    String name;  //名前

    @Size(max=32)
    String lab;  //研究室

    @Size(max=32)
    String phone;  //電話番号

    @NotBlank
    @Email
    String email; //メール
    
    Role role; //ロール

    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt; //作成日時

    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt; //更新日時

    public enum Role {
        TEACHER,
        ADMIN
    }


}