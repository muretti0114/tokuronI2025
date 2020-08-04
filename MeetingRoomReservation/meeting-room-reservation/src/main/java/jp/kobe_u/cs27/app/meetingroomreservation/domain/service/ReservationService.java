package jp.kobe_u.cs27.app.meetingroomreservation.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.ReservationDto;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Reservation;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.ReservationRepository;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.RoomRepository;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.UserRepository;

@Service
public class ReservationService {
    @Autowired
    ReservationRepository rRepo;
    @Autowired
    UserRepository users;
    @Autowired
    RoomRepository rooms;

    /**
     * 指定したユーザの予約を取得する（カレンダー用）
     * 
     * @param uid
     * @return
     */
    public List<ReservationDto> getMyCalendar(String uid) {
        Iterable<Reservation> all = rRepo.findByUid(uid);
        User user = getUser(uid);
        ArrayList<ReservationDto> list = new ArrayList<>();
        for (Reservation r : all) {
            Room room = getRoom(r.getRid());
            list.add(ReservationDto.build(r, room, user));
        }

        return list;
    }

    /**
     * 指定した日付の予約を取得する
     * 
     * @param date
     * @return
     */
    public List<ReservationDto> getReservationsByDate(Date date) {
        Iterable<Reservation> all = rRepo.findByDate(date);
        ArrayList<ReservationDto> list = new ArrayList<>();
        for (Reservation r : all) {
            Room room = getRoom(r.getRid());
            User user = getUser(r.getUid());
            list.add(ReservationDto.build(r, room, user));
        }

        return list;
    }

    /**
     * 予約番号で予約を取得する
     */
    public Reservation getReservationByNumber(Long number) {
        Reservation r = rRepo.findById(number)
                .orElseThrow(() -> new YoyakuAppException(YoyakuAppException.RESERVATION_NOT_FOUND,
                        number + ": No sush reservation"));
        return r;
    }
    /**
     * 予約番号で予約Dtoを取得する
     */
    public ReservationDto getReservationDto(Long number) {
        
        Reservation r = getReservationByNumber(number);
        Room room = getRoom(r.getRid());
        User user = getUser(r.getUid());
        return ReservationDto.build(r, room, user);
    }
    /**
     * 指定した会議室の予約が可能かをチェックする
     * 
     * @param rid       会議室ID
     * @param date      予約する日
     * @param startTime 開始時刻
     * @param endTime   終了時刻
     * @return
     */
    public boolean isVacant(Long rid, Date date, Date startTime, Date endTime) {
        Long count = rRepo.countAlreadyBooked(rid, date, startTime, endTime);

        return (count == 0) ? true : false;
    }

    /**
     * 予約を追加する
     * 
     * @param reservation
     * @return
     */
    public Reservation add(Reservation r) {
        // 開始・終了時刻のチェック
        if (r.getStartTime().compareTo(r.getEndTime()) >= 0) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_RESERVATION_DATE,
                    "startTime must be before endTime");
        }

        // 空きチェック
        if (!isVacant(r.getRid(), r.getDate(), r.getStartTime(), r.getEndTime())) {
            Room room = getRoom(r.getRid());
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_BOOKED,
                    "Room " + room.getRoomNumber() + " is already booked at " + r.getStartTime() + " to " + r.getEndTime());
        }
        // 作成時刻をセット
        r.setCreatedAt(new Date());
        return rRepo.save(r);
    }

    /**
     * 予約を変更する
     * 
     * @param uid
     * @param number
     * @param startTime
     * @param endTime
     * @return
     */
    public Reservation change(String uid, Long number, Date startTime, Date endTime) {
        // 予約を取得
        Reservation r = getReservationByNumber(number);

        // 権限チェック（自分の予約に限って変更可能）
        if (!uid.equals(r.getUid())) {
            throw new YoyakuAppException(YoyakuAppException.RESERVATION_NOT_PERMITTED,
                    uid + ": cannot update other's reservation.");
        }

        //いったん自分の予約を消してから
        rRepo.delete(r);
        // 空きチェック
        if (!isVacant(r.getRid(), r.getDate(), startTime, endTime)) {
            //埋まっていたら書き戻して例外を吐く
            rRepo.save(r);
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_BOOKED, ": Room already booked at this time");
        }
        // 更新時刻をセット
        r.setUpdatedAt(new Date());
        // 時刻を変更して
        r.setStartTime(startTime);
        r.setEndTime(endTime);
        // セーブ
        return rRepo.save(r);
    }

    /**
     * 予約をキャンセルする
     * 
     * @param uid
     * @param number
     */
    public void cancel(String uid, Long number) {
        Reservation r = getReservationByNumber(number);
        // 権限チェック
        if (!uid.equals(r.getUid())) {
            throw new YoyakuAppException(YoyakuAppException.RESERVATION_NOT_PERMITTED,
                    uid + ": cannot delete other's reservation.");
        }
        rRepo.delete(r);
    }

    /* ----------------- 以下，ユーティリティ関数 (private) ------------------- */
    /**
     * ユーザを取得する
     * 
     * @param uid
     * @return
     */
    private User getUser(String uid) {
        User user = users.findById(uid)
                .orElseThrow(() -> new YoyakuAppException(YoyakuAppException.USER_NOT_FOUND, uid + ": No such user"));
        return user;
    }

    /**
     * 会議室を取得する
     * 
     * @param rid
     * @return
     */
    private Room getRoom(Long rid) {
        Room room = rooms.findById(rid).orElseThrow(
                () -> new YoyakuAppException(YoyakuAppException.ROOM_NOT_FOUND, rid + ": Room not found."));
        return room;
    }

}