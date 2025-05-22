package jp.kobe_u.es4.app.meetingroomreservation.application.dto;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 会議室のDTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomDto {
    Long rid; //会議室ID
    String roomNumber; //部屋番号
    String name; //会議室名
    String building;  //建物名
    String description; //説明

    public static RoomDto build(Room r) {
        return new RoomDto(r.getRid(), r.getRoomNumber(), r.getName(), r.getBuilding(), r.getDescription());
    }
}