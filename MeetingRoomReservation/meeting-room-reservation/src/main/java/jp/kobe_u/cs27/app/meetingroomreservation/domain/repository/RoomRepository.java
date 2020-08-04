package jp.kobe_u.cs27.app.meetingroomreservation.domain.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;

/**
 * 会議室リポジトリ
 */
@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
}