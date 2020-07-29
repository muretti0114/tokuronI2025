package jp.kobe_u.cs27.app.meetingroomreservation.domain.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@Service
public class UserService {
    UserRepository users;
    private PasswordEncoder passwordEncoder;
    /**
     * ユーザを新規作成
     * @param user
     * @return
     */
    public User createUser(User user) {
        String uid = user.getUid();
        if (users.existsById(uid)) {
            throw new YoyakuAppException(YoyakuAppException.USER_ALREADY_EXISTS,
            uid + ": User already exists");
        }
        //パスワードを暗号化
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);
        user.setCreatedAt(new Date());
        return users.save(user);
    }
    /**
     * 全ユーザ取得
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
      * @param uid
      * @return
      */
    public User getUser(String uid) {
        User user = users.findById(uid).orElseThrow(
        () -> new YoyakuAppException(YoyakuAppException.USER_NOT_FOUND, uid + ": No such user")
        );
        return user;
    }

     /**
      * ユーザを更新
      * @param uid
      * @param user
      * @return
      */
     public User updateUser(String uid, User user) {
        User orig = getUser(uid);
        if (!uid.equals(user.getUid())) {
             throw new YoyakuAppException(YoyakuAppException.INVALID_USER_UPDATE,
             uid + ": uid cannot be updated");
        }
        //パスワードを暗号化
        String pass = passwordEncoder.encode(user.getPassword());
        user.setPassword(pass);
        user.setCreatedAt(orig.getCreatedAt());
        user.setUpdatedAt(new Date());
        return users.save(user);
     }

     /**
      * ユーザを消去する
      * @param uid
      */
     public void deleteUser(String uid) {
         User user  = getUser(uid);
         users.delete(user);
     }

}