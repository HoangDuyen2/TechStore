package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.dto.interfaces.OnCreate;
import hcmute.edu.vn.techstore.dto.interfaces.OnUpdate;
import hcmute.edu.vn.techstore.dto.interfaces.StaffGroup;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller("userAdminController")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final Validator validator;

    @GetMapping("")
    public String userList(Model model) {
        model.addAttribute("users", userService.getAllUsersNotContains(ERole.ROLE_ADMIN));
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
    public String showUserDetail(Model model, @PathVariable("id") Long id, @RequestParam(name = "status", required = false, defaultValue = "false") boolean status) {
        UserRequest userRequest = userService.getUserById(id);
        model.addAttribute("status",status);
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @PostMapping("/add-user")
    public String addNewUser(@ModelAttribute("new_user") UserRequest userRequest, BindingResult result, Model model) {
        result = checkBindingResult(result, userRequest, OnCreate.class);

        if(result.hasErrors()) {
            model.addAttribute("status",true);
            model.addAttribute("new_user", userRequest);
            return "admin/user/add-new-user";
        }
            try {
                if (userService.register(userRequest)){
                    return "redirect:/admin/users";
                }
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }

        model.addAttribute("status",true);
        model.addAttribute("new_user", userRequest);
        return "admin/user/add-new-user";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute("new_user") UserRequest userRequest, BindingResult result, Model model) {
        result = checkBindingResult(result, userRequest, OnUpdate.class);

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

    BindingResult checkBindingResult(BindingResult result, UserRequest userRequest, Class type) {
        Class<?>[] groups;

        if ("ROLE_STAFF".equalsIgnoreCase(userRequest.getRoleName())){
            groups = new Class[]{type, Default.class, StaffGroup.class};
        }else {
            groups = new Class[]{type, Default.class};
        }

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest, groups);

        for (ConstraintViolation<UserRequest> violation : violations) {
            result.rejectValue(violation.getPropertyPath().toString(), " ",violation.getMessage());
        }

        return result;
    }
}
