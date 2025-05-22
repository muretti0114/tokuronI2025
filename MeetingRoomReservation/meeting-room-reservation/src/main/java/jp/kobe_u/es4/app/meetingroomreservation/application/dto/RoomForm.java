package jp.kobe_u.es4.app.meetingroomreservation.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jp.kobe_u.es4.app.meetingroomreservation.domain.entity.Room;
import lombok.Data;

@Data
public class RoomForm {
    @NotBlank
    @Size(max=16)
    String roomNumber; //部屋番号．省略不可．16文字まで
    @Size(max=64)
    String name; //会議室名．64文字まで
    @Size(max=64)
    String building;  //建物名．64文字まで
    @Size(max=512)
    String description; //説明．512文字まで
    //エンティティに変換．IDや生成，更新時刻はnullで．
    public Room toEntity() {
        Room r = new Room(null, roomNumber, name, building, description, null, null);
        return r;
    }
}