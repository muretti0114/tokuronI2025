package jp.kobe_u.es4.app.meetingroomreservation.domain.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.Reservation;

/**
 * 予約リポジトリ
 */
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    // 日付で検索する
    public Iterable<Reservation> findByDate(Date date);
    // ユーザの全予約を取得する（カレンダー用）
    public Iterable<Reservation> findByUid(String uid);

    // ユーザのその月の予約を検索する（FullCalendarを使うことにしたので，不要）
    @Query(value = "SELECT * FROM reservation r WHERE (DATE_FORMAT(r.date, '%Y%m') = ?1) AND (r.uid = ?2))",  nativeQuery = true)
    public Iterable<Reservation> findByMonthAndUid(String yyyyMM, String uid);

    // 与えられた日時に重複している予約の個数を取得する
    @Query(value = "SELECT COUNT(r.rid) FROM reservation r WHERE (r.rid = ?1) AND (r.date = ?2) AND (( ?3 < r.start_time AND r.start_time <?4) OR (?3 < r.end_time AND r.end_time < ?4))",  nativeQuery = true)    
    public Long countAlreadyBooked(Long rid, Date  date, Date startTime, Date endTime);

}