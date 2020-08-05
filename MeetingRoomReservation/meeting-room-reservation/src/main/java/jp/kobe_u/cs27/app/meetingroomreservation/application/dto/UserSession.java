package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;

public class UserSession implements UserDetails {
    /**
     *
     */
    private static final long serialVersionUID = 5481546178797722247L;
    User user;

    public UserSession(User user) {
        this.user = user;
    }

    /**
     * 権限を取得
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        User.Role role = user.getRole();
        switch(role) {
            //教員の時は，教員権限を追加
            case TEACHER:
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
            break;

            //管理者の時は，教員権限と管理者権限を両方追加
            case ADMIN:
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            break;

            //それ以外はエラー
            default:
            throw new YoyakuAppException(YoyakuAppException.INVALID_USER_ROLE, 
                role + ": Invalid user role");
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUid();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getCallName() {
        return user.getName();
    }

    public String getLab() {
        return user.getLab();
    }

    public String getRole() {
        return user.getRole().name();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getPhone() {
        return user.getPhone();
    }

}