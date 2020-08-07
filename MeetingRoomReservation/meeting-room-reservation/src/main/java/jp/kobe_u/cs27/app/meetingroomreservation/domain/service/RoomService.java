package jp.kobe_u.cs27.app.meetingroomreservation.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.exception.YoyakuAppException;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.repository.RoomRepository;

@Service
public class RoomService {
    @Autowired
    RoomRepository rooms;

    /**
     * 会議室を作成する
     */
    public Room createRoom(Room room) {
        Long rid = room.getRid();
        // IDの重複検査
        if (rid!=null && rooms.existsById(rid)) {
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_EXISTS, 
            rid +": Room already exists.");
        }
        // 名前の重複検査
        String roomNumber = room.getRoomNumber();
        if (rooms.existsByRoomNumber(roomNumber)) {
            throw new YoyakuAppException(YoyakuAppException.ROOM_ALREADY_EXISTS, 
            roomNumber +": Room already exists.");
        }
        Date now = new Date();
        room.setCreatedAt(now);
        return rooms.save(room);
    }

    /**
     * 部屋をIDで取得する
     * @param rid
     * @return
     */
    public Room getRoom(Long rid) {
        Room room = rooms.findById(rid).orElseThrow(
            () -> new YoyakuAppException(YoyakuAppException.ROOM_NOT_FOUND, 
            rid + ": Room not found.")
        );
        return room;
    }

    /**
     * 全会議室情報を取得
     * @return
     */
    public List<Room> getAllRooms() {
        ArrayList<Room> list = new ArrayList<>();
        Iterable<Room> all = rooms.findAll();
        //リストに詰め替え
        all.forEach(list::add);
        return list;
    }

    /**
     * 会議を更新する
     * @param rid
     * @param r
     * @return
     */
    public Room updateRoom(Long rid, Room r) {
        Room orig = getRoom(rid);
        if (!rid.equals(r.getRid())) {
            throw new YoyakuAppException(YoyakuAppException.INVALID_ROOM_UPDATE,
             rid + ": Room ID does not match. Cannot update");
        }
        //      room.setRoomNumber(r.getRoomNumber());
        r.setCreatedAt(orig.getCreatedAt());
        r.setUpdatedAt(new Date());

        return rooms.save(r);
    }

    /**
     * 会議室を削除する
     * @param rid
     */
    public void deleteRoom(Long rid) {
        Room room = getRoom(rid);
        rooms.delete(room);
    }


}