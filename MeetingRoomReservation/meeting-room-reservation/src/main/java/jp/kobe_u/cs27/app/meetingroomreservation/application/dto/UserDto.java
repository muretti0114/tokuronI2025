package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザのDTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    String uid;  //ユーザID
    String name;  //名前
    String lab;  //研究室
    String phone;  //電話番号
    String email; //メール

    public static UserDto build(User u) {
        return new UserDto(u.getUid(), u.getName(), u.getLab(), u.getPhone(), u.getEmail());
    }
}