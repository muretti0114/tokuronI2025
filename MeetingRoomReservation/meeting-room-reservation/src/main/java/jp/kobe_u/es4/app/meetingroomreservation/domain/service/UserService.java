package jp.kobe_u.es4.app.meetingroomreservation.domain.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.kobe_u.es4.app.meetingroomreservation.application.dto.UserSession;
import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.es4.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.es4.app.meetingroomreservation.domain.repository.UserRepository;
import lombok.AllArgsConstructor;

/**
 * ユーザサービス．予約システムのユーザのCRUD処理と，認証・認可に必要なサービスを提供する
 * Spring securityの UserDetailsServiceを実走している
 */
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository users; //予約システムのユーザリポジトリ
    @Autowired
    private PasswordEncoder passwordEncoder; //システム共通のパスワードエンコーダ（ユーザ作成・更新時に利用）

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

    /**
     * ユーザIDで検索して，ユーザ詳細を返すサービス．Spring Securityから呼ばれる
     */
    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        // レポジトリからユーザを検索する
        User user = users.findById(uid)
                .orElseThrow(() -> new YoyakuAppException(YoyakuAppException.USER_NOT_FOUND, uid + ": No such user"));

        // ユーザのロールに応じて，権限を追加していく
        List<GrantedAuthority> authorities = new ArrayList<>(); // 権限リスト
        User.Role role = user.getRole(); // ユーザの権限
        switch (role) {
            // 教員の時は，教員権限を追加
            case TEACHER:
                // 権限文字列には，ROLE_を付けないといけない．
                authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
                break;

            // 管理者の時は，教員権限と管理者権限を両方追加
            case ADMIN:
                // 権限文字列には，ROLE_を付けないといけない．
                authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
                
            // それ以外はエラー
            default:
                throw new YoyakuAppException(YoyakuAppException.INVALID_USER_ROLE, role + ": Invalid user role");
        }

        // userからユーザセッション(UserDetailsの実装)を作成して返す
        UserSession userSession = new UserSession(user, authorities);

        return userSession;
    }

    /**
     * 管理者を登録する
     * @param uid
     * @param password
     */
    @Transactional
    public void registerAdmin(String uid, String password) {
        User user = new User(uid, passwordEncoder.encode(password), "システム管理者", "オフィス", null,
                "yoyaku-admin@localhost", User.Role.ADMIN, new Date(), new Date());
        users.save(user);
    }
}