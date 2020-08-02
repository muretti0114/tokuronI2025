package jp.kobe_u.cs27.app.meetingroomreservation.domain.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository users;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * ユーザを新規作成
     * 
     * @param user
     * @return
     */
    public User createUser(User user) {
        String uid = user.getUid();
        if (users.existsById(uid)) {
            throw new YoyakuAppException(YoyakuAppException.USER_ALREADY_EXISTS, uid + ": User already exists");
        }
        // パスワードを暗号化
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);
        user.setCreatedAt(new Date());
        return users.save(user);
    }

    /**
     * 全ユーザ取得
     * 
     * @return
     */
    public List<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        Iterable<User> all = users.findAll();
        all.forEach(list::add);
        return list;
    }

    /**
     * ユーザをIDで取得
     * 
     * @param uid
     * @return
     */
    public User getUser(String uid) {
        User user = users.findById(uid)
                .orElseThrow(() -> new YoyakuAppException(YoyakuAppException.USER_NOT_FOUND, uid + ": No such user"));
        return user;
    }

    /**
     * ユーザを更新
     * 
     * @param uid
     * @param user
     * @return
     */
    public User updateUser(String uid, User user) {
        User orig = getUser(uid);
        if (!uid.equals(user.getUid())) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_USER_UPDATE, uid + ": uid cannot be updated");
        }
        // パスワードを暗号化
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);
        user.setCreatedAt(orig.getCreatedAt());
        user.setUpdatedAt(new Date());
        return users.save(user);
    }

    /**
     * ユーザを消去する
     * 
     * @param uid
     */
    public void deleteUser(String uid) {
        User user = getUser(uid);
        users.delete(user);
    }

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