package jp.kobe_u.cs27.app.meetingroomreservation.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import lombok.Data;

@Data
public class RoomForm {
    @NotBlank
    @Size(max=16)
    String roomNumber; //部屋番号

    @Size(max=64)
    String name; //会議室名

    @Size(max=64)
    String building;  //建物名

    @Size(max=512)
    String description; //説明
    
    public Room toEntity() {
        Room r = new Room(null, roomNumber, name, building, description, null, null);
        return r;
    }
}