package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.model.request.UserRequest;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller("userAdminController")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("")
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/all-user";
    }

    @GetMapping("/add-new-user")
    public String addUser(Model model) {
        UserRequest userRequest = new UserRequest();
        model.addAttribute("status",true);
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @GetMapping("/{id}")
    public String showUserDetail(Model model, @PathVariable("id") Long id, @RequestParam(name = "status", required = false, defaultValue = "false") boolean status, Model model1) {
        UserRequest userRequest = userService.getUserById(id);
        model.addAttribute("status",status);
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @PostMapping("/add-user")
    public String addNewUser(@Valid @ModelAttribute("new_user") UserRequest userRequest, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("status",true);
            model.addAttribute("new_user", userRequest);
            return "admin/user/add-new-user";
        }
        if(userRequest.getUserId() != null){
            try {
                if(userService.updateUser(userRequest)){
                    return "redirect:/admin/users";
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            if (userRequest.getPassword() == null||userRequest.getPassword().equals("")) {
                model.addAttribute("error","Please enter password");
                return "admin/user/add-new-user";
            }
            if (userRequest.getConfirmPassword() == null||userRequest.getConfirmPassword().equals("")) {
                model.addAttribute("error","Please enter confirm password");
                return "admin/user/add-new-user";
            }
            try {
                if (userService.register(userRequest)){
                    return "redirect:/admin/users";
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("status",true);
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @GetMapping("/toggle-active/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleActive(@PathVariable("id") Long id,
                                                            @RequestParam("active") boolean active) {
        Map<String, Object> response = new HashMap<>();

        try {
            UserRequest userRequest = userService.getUserById(id);
            if (userRequest.getRoleName() == "ROLE_ADMIN") {
                response.put("status", "error");
                response.put("message", "Can not change admin status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            userService.updateActived(id, active);

            response.put("status", "success");
            response.put("message", "User status has been updated.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
