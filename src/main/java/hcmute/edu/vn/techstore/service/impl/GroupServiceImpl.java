package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.entity.GroupEntity;
import hcmute.edu.vn.techstore.repository.GroupRepository;
import hcmute.edu.vn.techstore.service.interfaces.IGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
