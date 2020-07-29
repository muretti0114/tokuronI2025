package jp.kobe_u.cs27.app.meetingroomreservation.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    UserRepository users;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {

        User user = users.findById(uid)
                .orElseThrow(() -> new YoyakuAppException(YoyakuAppException.USER_NOT_FOUND, uid + ": No such user"));

        // 権限のリスト
        // AdminやUserなどが存在するが、今回は利用しないのでUSERのみを仮で設定
        // 権限を利用する場合は、DB上で権限テーブル、ユーザ権限テーブルを作成し管理が必要
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
        grantList.add(authority);

        // rawDataのパスワードは渡すことができないので、暗号化
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // userからユーザセッションを作成して返す
        UserDetails userDetails = new UserSession(user);

        return userDetails;
    }

    // adminを登録するメソッド
    @Transactional
    public void registerAdmin(String uid, String password, String email) {
        User user = new User(uid, passwordEncoder.encode(password), 
        "System Administrator", "Office",
         null, email, User.Role.ADMIN, new Date(), new Date());
        users.save(user);
    }

}