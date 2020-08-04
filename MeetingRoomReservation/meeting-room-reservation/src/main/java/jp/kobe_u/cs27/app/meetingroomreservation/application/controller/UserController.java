package jp.kobe_u.cs27.app.meetingroomreservation.application.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.kobe_u.cs27.app.meetingroomreservation.application.dto.UserForm;
import jp.kobe_u.cs27.app.meetingroomreservation.application.utils.YoyakuUtils;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.entity.User;
import jp.kobe_u.cs27.app.meetingroomreservation.domain.service.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {
    UserService us;

    /*--------------------------- UC7,8: ユーザを管理する ------------------------------*/
    @GetMapping("")
    String showList(Model model) {
        List<User> userList = us.getAllUsers();
        model.addAttribute("userList", userList);
        model.addAttribute("user", YoyakuUtils.getUserSession());

        return "user/list";
    }

    /*--------------------- ユーザを作成する --------------------- */
    @GetMapping("/create")  
    String showForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("user", YoyakuUtils.getUserSession());

        return "user/form";
    }

    @PostMapping("")  
    String create(@ModelAttribute("userForm") @Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.err.println(result);
            model.addAttribute("user", YoyakuUtils.getUserSession());

            return "user/form";
        }
        us.createUser(form.toEntity());
        return "redirect:/users";
    }


    /*--------------------- ユーザを更新する --------------------- */
    @GetMapping("/{uid}")
    String showUser(@PathVariable String uid, Model model) {
        User user = us.getUser(uid);
        UserForm form = UserForm.toForm(user);
        model.addAttribute("userForm", form);
        model.addAttribute("uid", uid);
        model.addAttribute("user", YoyakuUtils.getUserSession());


        return "user/update";
    }
    @PostMapping("/{uid}")  
    String update(@PathVariable String uid, @ModelAttribute("userForm") @Validated UserForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.err.println(result);
            model.addAttribute("uid", uid);
            model.addAttribute("user", YoyakuUtils.getUserSession());

            return "user/update";
        }
        us.updateUser(uid, form.toEntity());
        return "redirect:/users";
    }

    /*--------------------- ユーザを削除する --------------------- */
    @GetMapping("/{uid}/delete")  
    String delete(@PathVariable String uid, Model model) {
        us.deleteUser(uid);
      
        return "redirect:/users";
    }

  
}