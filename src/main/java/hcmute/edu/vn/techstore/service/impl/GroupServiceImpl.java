package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.dto.response.GroupResponse;
import hcmute.edu.vn.techstore.entity.GroupEntity;
import hcmute.edu.vn.techstore.repository.GroupRepository;
import hcmute.edu.vn.techstore.service.interfaces.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupRepository groupRepository;

    @Override
    public boolean isGroupNameExists(String name) {
        return groupRepository.existsByName(name);
    }

    @Override
    public boolean createGroup(GroupCreateRequest groupCreateRequest) {
        GroupEntity groupEntity = GroupEntity.builder()
                .name(groupCreateRequest.getName())
                .description(groupCreateRequest.getDescription())
                .build();
        groupRepository.save(groupEntity);
        return true; // Group created successfully
    }

    @Override
    public Page<GroupResponse> getAllGroups(int page, int size) {
        Page<GroupEntity> groupEntities = groupRepository.findAll(PageRequest.of(page, size));
        return groupEntities.map(groupEntity -> GroupResponse.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .description(groupEntity.getDescription())
                .createdAt(groupEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .updatedAt(groupEntity.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
    }
}
