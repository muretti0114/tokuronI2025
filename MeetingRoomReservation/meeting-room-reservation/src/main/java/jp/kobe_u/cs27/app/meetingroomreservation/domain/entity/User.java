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
 * ユーザ・エンティティ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    String uid;

    @NotBlank
    @Size(max=128)
    String password;

    @NotBlank
    @Size(max=32)
    String name;

    @Size(max=32)
    String lab;

    @Size(max=32)
    String phone;

    @NotBlank
    @Email
    String email;
    
    Role role;

    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    Date updatedAt;

    public enum Role {
        TEACHER,
        ADMIN
    }


}