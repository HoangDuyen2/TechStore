package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.response.UserSearchResponse;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserServiceImpl")
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("Tests for searchUsers method")
    class SearchUsers {
        @Test
        @DisplayName("Should return empty list when no users match the search criteria")
        void testSearchUsers_NoMatches() {
            // Given
            String keyword = "nonexistent";

            // Mock behavior
            when(userRepository.findAll(any(Specification.class))).thenReturn(List.of());

            // When
            List<UserSearchResponse> result = userService.searchUsers(keyword);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userRepository, times(1)).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return list of users matching the search criteria")
        void testSearchUsers_WithMatches() {
            // Given
            String keyword = "john";
            UserEntity user1 = UserEntity.builder()
                    .id(1L)
                    .account(AccountEntity.builder()
                            .email("john@gmail.com")
                            .build())
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("123456789")
                    .build();
            UserEntity user2 = UserEntity.builder()
                    .id(2L)
                    .account(AccountEntity.builder()
                            .email("johnny@gmail.com")
                            .build())
                    .firstName("Johnny")
                    .lastName("Smith")
                    .phoneNumber("987654321")
                    .build();
            List<UserEntity> mockUsers = List.of(user1, user2);

            // Mock behavior
            when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

            // When
            List<UserSearchResponse> result = userService.searchUsers(keyword);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("John Doe", result.getFirst().getFullName());
            assertEquals("Johnny Smith", result.getLast().getFullName());
            assertEquals("john@gmail.com", result.getFirst().getEmail());
            assertEquals("johnny@gmail.com", result.getLast().getEmail());
            assertEquals("123456789", result.getFirst().getPhone());
            assertEquals("987654321", result.getLast().getPhone());
            verify(userRepository, times(1)).findAll(any(Specification.class));
        }
    }
}