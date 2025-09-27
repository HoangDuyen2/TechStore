package hcmute.edu.vn.techstore.controller.admin;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.dto.request.GroupUpdateRequest;
import hcmute.edu.vn.techstore.dto.response.GroupDetailResponse;
import hcmute.edu.vn.techstore.dto.response.GroupResponse;
import hcmute.edu.vn.techstore.service.interfaces.IGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/groups")
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

    @GetMapping("")
    public String index(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        Page<GroupResponse> groupResponses = groupService.getAllGroups(page, size);
        model.addAttribute("groupResponses", groupResponses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", groupResponses.getTotalPages());
        return "admin/group/group-list";
    }

    @GetMapping("/{id}")
    public String viewGroupDetails(@PathVariable Long id, Model model) {
        GroupDetailResponse groupDetailResponse = groupService.getGroupDetailById(id);
        model.addAttribute("group", groupDetailResponse);
        return "admin/group/group-detail";
    }

    @GetMapping("/add")
    public String addGroup(Model model) {
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest();
        model.addAttribute("group", groupCreateRequest);
        return "admin/group/new-group";
    }

    @GetMapping("/edit/{id}")
    public String viewGroupUpdate(@PathVariable Long id, Model model) {
        GroupUpdateRequest groupUpdateRequest = groupService.getGroupUpdateRequestById(id);
        model.addAttribute("id", id);
        model.addAttribute("groupUpdateRequest", groupUpdateRequest);
        return "admin/group/update-group";
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable Long id, Model model) {
        Map<String, String> response = new HashMap<>();
        if (groupService.deleteGroup(id)) {
            response.put("status", "success");
            response.put("message", "Group deleted successfully.");
            return org.springframework.http.ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to delete the group.");
            return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/list-groups")
    public ResponseEntity<?> listGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PostMapping("/insert")
    public String insertGroup(Model model,
                              @Valid @ModelAttribute("group") GroupCreateRequest groupCreateRequest,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            return "admin/group/new-group";
        }
        String msg;
        boolean isCreated = groupService.createGroup(groupCreateRequest);
        if (isCreated) {
            msg = "Group created successfully!";
            model.addAttribute("success", msg);
        } else {
            msg = "Group name already exists!";
            model.addAttribute("error", msg);
        }
        return "admin/group/new-group";
    }

    @PostMapping("/update/{id}")
    public String updateGroup(@PathVariable Long id,
                              @Valid @ModelAttribute("groupUpdateRequest") GroupUpdateRequest groupUpdateRequest,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            model.addAttribute("id", id);
            model.addAttribute("groupUpdateRequest", groupUpdateRequest);
            return "admin/group/update-group";
        }
        String msg;
        boolean isUpdated = groupService.updateGroup(id, groupUpdateRequest);
        if (isUpdated) {
            msg = "Group updated successfully!";
            model.addAttribute("success", msg);
        } else {
            msg = "Failed to update group. Group name might already exist or group not found.";
            model.addAttribute("error", msg);
        }
        model.addAttribute("id", id);
        model.addAttribute("groupUpdateRequest", groupUpdateRequest);
        return "admin/group/update-group";
    }

    @PostMapping("/{id}/add-user")
    public ResponseEntity<Map<String, String>> addUserToGroup(@PathVariable Long id, @RequestParam Long selectedUserId) {
        Map<String, String> response = new HashMap<>();
        if (groupService.isUserInGroup(id, selectedUserId)) {
            response.put("status", "error");
            response.put("message", "User is already in the group.");
            return ResponseEntity.badRequest().body(response);
        }
        boolean isAdded = groupService.addUserToGroup(id, selectedUserId);
        if (isAdded) {
            response.put("status", "success");
            response.put("message", "User added to group successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add user to group. User or group might not exist.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{id}/remove-user")
    public ResponseEntity<Map<String, String>> removeUserFromGroupPost(@PathVariable Long id, @RequestParam Long userId) {
        Map<String, String> response = new HashMap<>();
        boolean isRemoved = groupService.removeUserFromGroup(id, userId);
        if (isRemoved) {
            response.put("status", "success");
            response.put("message", "User removed from group successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to remove user from group. User or group might not exist.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
