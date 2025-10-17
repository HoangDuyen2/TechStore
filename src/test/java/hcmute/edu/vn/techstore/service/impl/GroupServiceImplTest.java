package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.GroupCreateRequest;
import hcmute.edu.vn.techstore.dto.request.GroupUpdateRequest;
import hcmute.edu.vn.techstore.entity.GroupEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.GroupRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GroupServiceImpl Test")
class GroupServiceImplTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private UserEntity user;
    private GroupEntity group;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setGroups(new HashSet<>());

        group = new GroupEntity();
        group.setId(2L);
        group.setUsers(new HashSet<>());
    }

    @Nested // Use @Nested to group related tests
    @DisplayName("Test isUserInGroup method")
    class IsUserInGroup {
        @Test
        @DisplayName("User is in the group")
        void testIsUserInGroup_UserInGroup() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;
            group.getUsers().add(user);
            user.getGroups().add(group);

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertTrue(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
        }

        @Test
        @DisplayName("User is not in the group")
        void testIsUserInGroup_UserNotInGroup() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(new GroupEntity()));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
        }

        @Test
        @DisplayName("User not found")
        void testIsUserInGroup_UserNotFound() {
            // Given
            Long userId = 10L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
        }

        @Test
        @DisplayName("Group not found")
        void testIsUserInGroup_GroupNotFound() {
            // Given
            Long userId = 1L;
            Long groupId = 10L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
        }
    }

    @Nested
    @DisplayName("Test addUserToGroup method")
    class AddUserToGroup {
        @Test
        @DisplayName("Add user to group successfully")
        void testAddUserToGroup_Success() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.addUserToGroup(groupId, userId);

            // Then
            assertTrue(result);
            assertTrue(group.getUsers().contains(user));
            assertTrue(user.getGroups().contains(group));
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).save(group);
            verify(userRepository, atMostOnce()).save(user);
        }

        @Test
        @DisplayName("Add user to group fails when user already in group")
        void testAddUserToGroup_UserAlreadyInGroup() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Add user to group to simulate existing membership
            group.getUsers().add(user);
            user.getGroups().add(group);

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.addUserToGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Add user to group fails when user not found")
        void testAddUserToGroup_UserNotFound() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            boolean result = groupService.addUserToGroup(groupId, userId);

            assertFalse(result);
            verify(groupRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Add user to group fails when group not found")
        void testAddUserToGroup_GroupNotFound() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            boolean result = groupService.addUserToGroup(groupId, userId);

            assertFalse(result);
            verify(groupRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Test removeUserFromGroup method")
    class RemoveUserFromGroup {
        @Test
        @DisplayName("Remove user from group successfully")
        void testRemoveUserFromGroup_Success() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;
            group.getUsers().add(user);
            user.getGroups().add(group);

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.removeUserFromGroup(groupId, userId);

            // Then
            assertTrue(result);
            assertFalse(group.getUsers().contains(user));
            assertFalse(user.getGroups().contains(group));
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).save(group);
            verify(userRepository, atMostOnce()).save(user);
        }

        @Test
        @DisplayName("Remove user from group fails when user not found")
        void testRemoveUserFromGroup_UserNotFound() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.removeUserFromGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Remove user from group fails when group not found")
        void testRemoveUserFromGroup_GroupNotFound() {
            // Given
            Long userId = 1L;
            Long groupId = 2L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            boolean result = groupService.removeUserFromGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository, atMostOnce()).findById(userId);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Test createGroup method")
    class CreateGroup {
        @Test
        @DisplayName("Create group successfully")
        void testCreateGroup_Success() {
            // Given
            GroupCreateRequest request = new GroupCreateRequest();
            request.setName("test");
            request.setDescription("This is a test group");

            // Mock repository behavior
            when(groupRepository.existsByName(request.getName())).thenReturn(false);

            // When
            boolean result = groupService.createGroup(request);

            // Then
            assertTrue(result);
            verify(groupRepository, atMostOnce()).existsByName(request.getName());
            verify(groupRepository, atMostOnce()).save(any(GroupEntity.class));
        }

        @Test
        @DisplayName("Create group fails when group name already exists")
        void testCreateGroup_GroupNameExists() {
            // Given
            GroupCreateRequest request = new GroupCreateRequest();
            request.setName("test");
            request.setDescription("This is a test group");

            // Mock repository behavior
            when(groupRepository.existsByName(request.getName())).thenReturn(true);

            // When
            boolean result = groupService.createGroup(request);

            // Then
            assertFalse(result);
            verify(groupRepository, atMostOnce()).existsByName(request.getName());
            verify(groupRepository, never()).save(any(GroupEntity.class));
        }
    }

    @Nested
    @DisplayName("Test getAllGroups method")
    class GetAllGroups {
        @Test
        @DisplayName("Get all groups successfully")
        void testGetAllGroups_Success() {
            // When
            groupService.getAllGroups();

            // Then
            verify(groupRepository, atMostOnce()).findAll();
        }
    }

    @Nested
    @DisplayName("Test getGroupDetailById method")
    class GetGroupDetailById {
        @Test
        @DisplayName("Get group detail by ID successfully")
        void testGetGroupDetailById_Success() {
            // Given
            Long groupId = 2L;

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            groupService.getGroupDetailById(groupId);

            // Then
            verify(groupRepository, atMostOnce()).findById(groupId);
        }

        @Test
        @DisplayName("Get group detail by ID fails when group not found")
        void testGetGroupDetailById_GroupNotFound() {
            // Given
            Long groupId = 10L;

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            groupService.getGroupDetailById(groupId);

            // Then
            verify(groupRepository, atMostOnce()).findById(groupId);
        }
    }

    @Nested
    @DisplayName("Test getAllGroups with pagination method")
    class GetAllGroupsWithPagination {
        @Test
        @DisplayName("Get all groups with pagination successfully")
        void testGetAllGroupsWithPagination_Success() {
            // Given
            int page = 0;
            int size = 10;

            // When
            groupService.getAllGroups(page, size);

            // Then
            verify(groupRepository, atMostOnce()).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Test getGroupUpdateRequestById method")
    class GetGroupUpdateRequestById {
        @Test
        @DisplayName("Get group update request by ID successfully")
        void testGetGroupUpdateRequestById_Success() {
            // Given
            Long groupId = 2L;

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            groupService.getGroupUpdateRequestById(groupId);

            // Then
            verify(groupRepository, atMostOnce()).findById(groupId);
        }

        @Test
        @DisplayName("Get group update request by ID fails when group not found")
        void testGetGroupUpdateRequestById_GroupNotFound() {
            // Given
            Long groupId = 10L;

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            groupService.getGroupUpdateRequestById(groupId);

            // Then
            verify(groupRepository, atMostOnce()).findById(groupId);
        }
    }

    @Nested
    @DisplayName("Test updateGroup method")
    class UpdateGroup {
        @Test
        @DisplayName("Update group successfully when group exists and new name is unique")
        void testUpdateGroup_Success_NameUnique() {
            // Given
            Long groupId = 2L;
            GroupUpdateRequest request = GroupUpdateRequest.builder()
                    .name("test")
                    .description("This is a test group")
                    .build();

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(groupRepository.findByName(request.getName())).thenReturn(null);

            // When
            boolean result = groupService.updateGroup(groupId, request);

            // Then
            assertTrue(result);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).existsByName(request.getName());
            verify(groupRepository, atMostOnce()).save(any(GroupEntity.class));
        }

        @Test
        @DisplayName("Update group successfully when group exists and name is unchanged")
        void testUpdateGroup_Success_NameUnchanged() {
            // Given
            Long groupId = 2L;
            group.setName("test"); // Existing group name
            GroupUpdateRequest request = GroupUpdateRequest.builder()
                    .name("test") // Same name as existing
                    .description("Updated description")
                    .build();

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(groupRepository.findByName(request.getName())).thenReturn(group); // Returns the same group

            // When
            boolean result = groupService.updateGroup(groupId, request);

            // Then
            assertTrue(result);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).findByName(request.getName());
            verify(groupRepository, atMostOnce()).save(any(GroupEntity.class));
        }

        @Test
        @DisplayName("Update group fails when group not found")
        void testUpdateGroup_GroupNotFound() {
            // Given
            Long groupId = 10L;
            GroupUpdateRequest request = GroupUpdateRequest.builder()
                    .name("updatedName")
                    .description("Updated description")
                    .build();

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            boolean result = groupService.updateGroup(groupId, request);

            // Then
            assertFalse(result);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, never()).existsByName(anyString());
            verify(groupRepository, never()).save(any(GroupEntity.class));
        }

        @Test
        @DisplayName("Update group fails when new group name already exists")
        void testUpdateGroup_GroupNameExists() {
            // Given
            Long groupId = 2L;
            GroupEntity existingGroup = new GroupEntity();
            existingGroup.setId(3L); // Different ID to simulate another group with same name
            existingGroup.setName("existingName");
            GroupUpdateRequest request = GroupUpdateRequest.builder()
                    .name("existingName") // Name that already exists
                    .description("Updated description")
                    .build();

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
            when(groupRepository.findByName(request.getName())).thenReturn(existingGroup);

            // When
            boolean result = groupService.updateGroup(groupId, request);

            // Then
            assertFalse(result);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).existsByName(request.getName());
            verify(groupRepository, never()).save(any(GroupEntity.class));
        }
    }

    @Nested
    @DisplayName("Test deleteGroup method")
    class DeleteGroup {
        @Test
        @DisplayName("Delete group successfully when group exists")
        void testDeleteGroup_Success() {
            // Given
            Long groupId = 2L;
            UserEntity user1 = new UserEntity();
            user1.setId(3L);
            UserEntity user2 = new UserEntity();
            user2.setId(4L);
            group.getUsers().add(user1);
            group.getUsers().add(user2);
            user1.getGroups().add(group);
            user2.getGroups().add(group);

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.deleteGroup(groupId);

            // Then
            assertTrue(result);
            assertFalse(user1.getGroups().contains(group));
            assertFalse(user2.getGroups().contains(group));
            assertTrue(group.getUsers().isEmpty());
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, atMostOnce()).delete(group);
        }

        @Test
        @DisplayName("Delete group fails when group not found")
        void testDeleteGroup_GroupNotFound() {
            // Given
            Long groupId = 10L;

            // Mock repository behavior
            when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

            // When
            boolean result = groupService.deleteGroup(groupId);

            // Then
            assertFalse(result);
            verify(groupRepository, atMostOnce()).findById(groupId);
            verify(groupRepository, never()).delete(any(GroupEntity.class));
        }
    }

    @Nested
    @DisplayName("Test deleteGroups method")
    class DeleteGroups {
        @Test
        @DisplayName("Delete multiple groups successfully")
        void testDeleteGroups_Success() {
            // Given
            Long groupId1 = 2L;
            Long groupId2 = 3L;
            GroupEntity group2 = new GroupEntity();
            group2.setId(groupId2);
            UserEntity user1 = new UserEntity();
            user1.setId(4L);
            UserEntity user2 = new UserEntity();
            user2.setId(5L);
            group.getUsers().add(user1);
            group2.getUsers().add(user2);
            user1.getGroups().add(group);
            user2.getGroups().add(group2);

            // Mock repository behavior
            when(groupRepository.findAllById(anyList())).thenReturn(java.util.List.of(group, group2));

            // When
            boolean result = groupService.deleteGroups(java.util.List.of(groupId1, groupId2));

            // Then
            assertTrue(result);
            verify(groupRepository, atMost(2)).findById(anyLong());
            verify(groupRepository, atMostOnce()).deleteAll(anyList());
        }

        @Test
        @DisplayName("Delete multiple groups fails when no groups found")
        void testDeleteGroups_NoGroupsFound() {
            // Given
            Long groupId1 = 10L;
            Long groupId2 = 11L;

            // Mock repository behavior
            when(groupRepository.findAllById(anyList())).thenReturn(java.util.List.of());

            // When
            boolean result = groupService.deleteGroups(java.util.List.of(groupId1, groupId2));

            // Then
            assertFalse(result);
            verify(groupRepository, atMostOnce()).findAllById(anyList());
            verify(groupRepository, never()).deleteAll(anyList());
        }
    }
}