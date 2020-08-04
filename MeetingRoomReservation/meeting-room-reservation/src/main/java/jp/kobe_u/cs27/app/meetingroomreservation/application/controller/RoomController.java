package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.RoomForm;
import jp.kobe_u.cs27.app.meetingroomreservation.application.utils.YoyakuUtils;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.Room;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.RoomService;

/**
 * 会議室コントローラ
 */
@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    RoomService rs;

    /*--------------------------- UC9,10: 会議室を管理する ------------------------------*/
    @GetMapping("")
    public String showList(Model model) {
        List<Room> list = rs.getAllRooms();
        model.addAttribute("roomList", list);
        model.addAttribute("user", YoyakuUtils.getUserSession());

        return "room/list";
    }

    /*------------------ 会議室を作成する -------------------------*/
    @GetMapping("/create")
    public String showForm(Model model) {
        RoomForm form = new RoomForm();
        model.addAttribute("roomForm", form);
        model.addAttribute("user", YoyakuUtils.getUserSession());

        return "room/form";
    }
    @PostMapping("")
    public String create(@ModelAttribute("roomForm") @Validated RoomForm form,
    BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", YoyakuUtils.getUserSession());
            return "room/form";
        }

        rs.createRoom(form.toEntity());
        //リスト画面に遷移
        return "redirect:/rooms";
    }

    /*------------------ 会議室を更新する -------------------------*/   
    @GetMapping("/{rid}")
    public String showInfo(@PathVariable  Long rid, Model model) {
        Room room = rs.getRoom(rid);
        model.addAttribute("roomForm", room);
        model.addAttribute("user", YoyakuUtils.getUserSession());

        return "room/update";
    }

    @PostMapping("/{rid}")
    public String update(@PathVariable  Long rid, @ModelAttribute("roomForm") @Validated Room room, 
    BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", YoyakuUtils.getUserSession());

            return "room/update";
        }

        rs.updateRoom(rid, room);

        //リスト画面に遷移
        return "redirect:/rooms";  
    }

    /*------------------ 会議室を削除する -------------------------*/   
    @GetMapping("/{rid}/delete")
    String delete(@PathVariable Long rid) {
        rs.deleteRoom(rid);
        return "redirect:/rooms";        
    }
    
}