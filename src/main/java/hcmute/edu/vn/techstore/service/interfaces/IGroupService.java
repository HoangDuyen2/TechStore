package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;

public interface IGroupService {
    boolean isGroupNameExists(String name);

    boolean createGroup(GroupCreateRequest groupCreateRequest);
}
