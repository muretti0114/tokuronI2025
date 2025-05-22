package jp.kobe_u.es4.app.meetingroomreservation.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserForm {
    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = "[a-z0-9_\\-]+")
    String uid;

    @NotBlank
    @Size(min=8, message = "パスワードは8文字以上")
    String password;

    @NotBlank
    @Size(min=8, message = "パスワードは8文字以上")
    String password2;

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

    public User toEntity() {
        return new User(uid, password, name, lab, phone, email, User.Role.TEACHER, null, null);
    }

    public static UserForm toForm(User user) {
        return new UserForm(user.getUid(), user.getPassword(), user.getPassword(), user.getName(),
        user.getLab(), user.getPhone(), user.getEmail());
    } 

}