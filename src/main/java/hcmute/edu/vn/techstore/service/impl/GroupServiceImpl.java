package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.dto.request.GroupUpdateRequest;
import hcmute.edu.vn.techstore.dto.response.GroupDetailResponse;
import hcmute.edu.vn.techstore.dto.response.GroupResponse;
import hcmute.edu.vn.techstore.dto.response.UserSearchResponse;
import hcmute.edu.vn.techstore.entity.GroupEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.GroupRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public boolean isUserInGroup(Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        if (user == null || group == null) {
            return false; // User or Group not found
        }
        return group.getUsers().contains(user);
    }

    @Override
    public boolean addUserToGroup(Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        if (user == null || group == null) {
            return false; // User or Group not found
        }

        if (group.getUsers().contains(user)) {
            return false; // User already in the group
        }

        // Update both sides of the relationship
        group.getUsers().add(user);
        user.getGroups().add(group);

        // Save both entities
        groupRepository.save(group);
        userRepository.save(user);

        return true;
    }

    @Override
    public boolean removeUserFromGroup(Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        GroupEntity group = groupRepository.findById(groupId).orElse(null);
        if (user == null || group == null) {
            return false; // User or Group not found
        }

        // Update both sides of the relationship
        group.getUsers().remove(user);
        user.getGroups().remove(group);

        // Save both entities
        groupRepository.save(group);
        userRepository.save(user);

        return true;
    }

    @Override
    public boolean createGroup(GroupCreateRequest groupCreateRequest) {
        if (groupRepository.existsByName(groupCreateRequest.getName())) {
            return false; // Group name already exists
        }
        GroupEntity groupEntity = GroupEntity.builder()
                .name(groupCreateRequest.getName())
                .description(groupCreateRequest.getDescription())
                .build();
        groupRepository.save(groupEntity);
        return true; // Group created successfully
    }

    @Override
    public Page<GroupResponse> getAllGroups(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString("DESC"), "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<GroupEntity> groupEntities = groupRepository.findAll(pageable);
        return groupEntities.map(groupEntity -> GroupResponse.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .description(groupEntity.getDescription())
                .build());
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        List<GroupEntity> groupEntities = groupRepository.findAll();
        return groupEntities.stream().map(groupEntity -> GroupResponse.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .description(groupEntity.getDescription())
                .build()).toList();
    }

    @Override
    public GroupDetailResponse getGroupDetailById(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElse(null);
        if (groupEntity == null) {
            return null; // Group not found
        }
        return GroupDetailResponse.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .description(groupEntity.getDescription())
                .users(groupEntity.getUsers().stream()
                        .map(user ->
                                UserSearchResponse.builder()
                                        .id(user.getId())
                                        .email(user.getAccount().getEmail())
                                        .phone(user.getPhoneNumber())
                                        .fullName(user.getFirstName() + " " + user.getLastName())
                                        .build()
                        )
                        .toList())
                .build();
    }

    @Override
    public GroupUpdateRequest getGroupUpdateRequestById(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElse(null);
        if (groupEntity == null) {
            return null; // Group not found
        }
        return GroupUpdateRequest.builder()
                .name(groupEntity.getName())
                .description(groupEntity.getDescription())
                .build();
    }

    @Override
    public boolean updateGroup(Long id, GroupUpdateRequest groupUpdateRequest) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElse(null);
        if (groupEntity == null) {
            return false; // Group not found
        }
        GroupEntity existingGroupWithName = groupRepository.findByName(groupUpdateRequest.getName());
        if (existingGroupWithName != null && !existingGroupWithName.getId().equals(id)) {
            return false; // Another group with the same name exists
        }
        groupEntity.setName(groupUpdateRequest.getName());
        groupEntity.setDescription(groupUpdateRequest.getDescription());
        groupRepository.save(groupEntity);
        return true; // Group updated successfully
    }

    @Override
    public boolean deleteGroup(Long id) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElse(null);
        if (groupEntity == null) {
            return false; // Group not found
        }
        groupRepository.delete(groupEntity);
        return true; // Group deleted successfully
    }
}
