package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.dto.request.GroupUpdateRequest;
import hcmute.edu.vn.techstore.dto.response.GroupDetailResponse;
import hcmute.edu.vn.techstore.dto.response.GroupResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IGroupService {
    boolean isUserInGroup(Long groupId, Long userId);

    boolean addUserToGroup(Long userId, Long groupId);

    boolean removeUserFromGroup(Long userId, Long groupId);

    boolean createGroup(GroupCreateRequest groupCreateRequest);

    Page<GroupResponse> getAllGroups(int page, int size);

    List<GroupResponse> getAllGroups();

    GroupDetailResponse getGroupDetailById(Long id);

    GroupUpdateRequest getGroupUpdateRequestById(Long id);

    boolean updateGroup(Long id, GroupUpdateRequest groupUpdateRequest);

    boolean deleteGroup(Long id);
}
