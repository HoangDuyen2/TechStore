package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.model.response.UserResponse;
import hcmute.edu.vn.techstore.service.IAccountService;
import hcmute.edu.vn.techstore.service.IRoleService;
import hcmute.edu.vn.techstore.service.IUserService;
import hcmute.edu.vn.techstore.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller("userAdminController")
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("")
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUsers());

        return "admin/user/all-user";
    }

    @GetMapping("/add-new-user")
    public String addUser(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @PostMapping("/add-user")
    public String addNewUser(@Valid @ModelAttribute("new_user") UserRequest userRequest, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("new_user", userRequest);
            return "admin/user/add-new-user";
        }
        if(userRequest.getUserId() != null){
//            try {
//                if(userService.register(userRequest)){
//                    return "redirect:/admin/user-list";
//                }
//            } catch (Exception e) {
//                model.addAttribute("error", e.getMessage());
//            }
        }
        else {
            try {
                if (userService.register(userRequest)){
                    return "redirect:/admin/users";
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }
}
