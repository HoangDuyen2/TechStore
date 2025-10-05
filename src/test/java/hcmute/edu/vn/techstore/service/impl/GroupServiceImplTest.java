package hcmute.edu.vn.techstore.service.impl;

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
            Long groupId = 3L;
            Long userId = 1L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertTrue(result);
            verify(userRepository).findById(userId);
            verify(groupRepository).findById(groupId);
        }

        @Test
        @DisplayName("User is not in the group")
        void testIsUserInGroup_UserNotInGroup() {
            // Given
            Long groupId = 1L;
            Long userId = 1L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(new GroupEntity()));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository).findById(userId);
            verify(groupRepository).findById(groupId);
        }

        @Test
        @DisplayName("User or Group not found")
        void testIsUserInGroup_UserOrGroupNotFound() {
            // Given
            Long groupId = 1L;
            Long userId = 10L;

            // Mock repository behavior
            when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(new GroupEntity()));

            // When
            boolean result = groupService.isUserInGroup(groupId, userId);

            // Then
            assertFalse(result);
            verify(userRepository).findById(userId);
            verify(groupRepository).findById(groupId);
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
            verify(userRepository).findById(userId);
            verify(groupRepository).findById(groupId);
            verify(groupRepository).save(group);
            verify(userRepository).save(user);
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
            verify(userRepository).findById(userId);
            verify(groupRepository).findById(groupId);
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
}