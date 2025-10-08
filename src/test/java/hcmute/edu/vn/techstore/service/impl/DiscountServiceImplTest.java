package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.dto.request.SendDiscountRequest;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.dto.response.GroupDetailResponse;
import hcmute.edu.vn.techstore.dto.response.UserSearchResponse;
import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.repository.DiscountRepository;
import hcmute.edu.vn.techstore.service.interfaces.IEmailService;
import hcmute.edu.vn.techstore.service.interfaces.IGroupService;
import hcmute.edu.vn.techstore.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for DiscountServiceImpl")
class DiscountServiceImplTest {
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private IUserService userService;
    @Mock
    private IGroupService groupService;
    @Mock
    private IEmailService emailService;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private DiscountEntity discount;
    private SendDiscountRequest sendDiscountRequest;

    @BeforeEach
    void setUp() {
        discount = DiscountEntity.builder()
                .id(1L)
                .name("Summer Sale")
                .code("SUMMER2024")
                .discountType(EDiscountType.COUPON)
                .amount(20L)
                .quantity(100)
                .build();

        sendDiscountRequest = new SendDiscountRequest();
        sendDiscountRequest.setDiscountId(1L);
        sendDiscountRequest.setUserIds(List.of(1L, 2L, 3L));
        sendDiscountRequest.setGroupIds(List.of(1L, 2L));
    }

    @Nested
    @DisplayName("Tests for checkDiscountQuantity method")
    class CheckDiscountQuantity {
        @Test
        @DisplayName("Should return true when total users is less than or equal to discount quantity")
        void testCheckDiscountQuantity_True() {
            // Given
            GroupDetailResponse groupDetailResponse1 = GroupDetailResponse.builder()
                    .id(1L)
                    .users(List.of(UserSearchResponse.builder().id(4L).build(), UserSearchResponse.builder().id(5L).build()))
                    .build();
            GroupDetailResponse groupDetailResponse2 = GroupDetailResponse.builder()
                    .id(2L)
                    .users(List.of(UserSearchResponse.builder().id(6L).build()))
                    .build();

            // Mock repository behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.of(discount));
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getFirst())).thenReturn(groupDetailResponse1);
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getLast())).thenReturn(groupDetailResponse2);

            // When
            boolean result = discountService.checkDiscountQuantity(sendDiscountRequest);

            // Then
            assertTrue(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
        }

        @Test
        @DisplayName("Should return false when discount not found")
        void testCheckDiscountQuantity_DiscountNotFound() {
            // Mock repository behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.empty());

            // When
            boolean result = discountService.checkDiscountQuantity(sendDiscountRequest);

            // Then
            assertFalse(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
        }

        @Test
        @DisplayName("Should return false when discount quantity is zero")
        void testCheckDiscountQuantity_DiscountQuantityZero() {
            // Given
            discount.setQuantity(0);

            // Mock repository behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.of(discount));

            // When
            boolean result = discountService.checkDiscountQuantity(sendDiscountRequest);

            // Then
            assertFalse(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
        }

        @Test
        @DisplayName("Should return false when total users exceed discount quantity")
        void testCheckDiscountQuantity_False() {
            // Given
            discount.setQuantity(5); // Set discount quantity to 5
            GroupDetailResponse groupDetailResponse1 = GroupDetailResponse.builder()
                    .id(1L)
                    .users(List.of(UserSearchResponse.builder().id(4L).build(), UserSearchResponse.builder().id(5L).build(), UserSearchResponse.builder().id(6L).build()))
                    .build();
            GroupDetailResponse groupDetailResponse2 = GroupDetailResponse.builder()
                    .id(2L)
                    .users(List.of(UserSearchResponse.builder().id(7L).build()))
                    .build();

            // Mock repository behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.of(discount));
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getFirst())).thenReturn(groupDetailResponse1);
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getLast())).thenReturn(groupDetailResponse2);

            // When
            boolean result = discountService.checkDiscountQuantity(sendDiscountRequest);

            // Then
            assertFalse(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
        }
    }

    @Nested
    @DisplayName("Tests for sendDiscount method")
    class SendDiscount {
        @Test
        @DisplayName("Should send discount emails successfully")
        void testSendDiscount_Success() {
            // Given
            UserRequest user1 = new UserRequest();
            user1.setUserId(1L);
            user1.setEmail("user1@gmail.com");
            UserRequest user2 = new UserRequest();
            user2.setUserId(2L);
            user2.setEmail("user2@gmail.com");
            UserRequest user3 = new UserRequest();
            user3.setUserId(3L);
            user3.setEmail("user3@gmail.com");
            GroupDetailResponse groupDetailResponse1 = GroupDetailResponse.builder()
                    .id(1L)
                    .users(List.of(
                            UserSearchResponse.builder().id(4L).email("user4@gmail.com").build(),
                            UserSearchResponse.builder().id(5L).email("user5@gmail.com").build()))
                    .build();
            GroupDetailResponse groupDetailResponse2 = GroupDetailResponse.builder()
                    .id(2L)
                    .users(List.of(UserSearchResponse.builder().id(6L).email("user6@gmail.com").build()))
                    .build();

            // Mock behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.of(discount));
            when(discountRepository.findByCode(discount.getCode())).thenReturn(discount);
            when(userService.getUserById(1L)).thenReturn(user1);
            when(userService.getUserById(2L)).thenReturn(user2);
            when(userService.getUserById(3L)).thenReturn(user3);
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getFirst())).thenReturn(groupDetailResponse1);
            when(groupService.getGroupDetailById(sendDiscountRequest.getGroupIds().getLast())).thenReturn(groupDetailResponse2);
            doNothing().when(emailService).sendDiscountCode(anyString(), anyString(), anyString(), anyString());

            // When
            boolean result = discountService.sendEmailDiscounts(sendDiscountRequest);

            // Then
            assertTrue(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
            verify(emailService, times(6)).sendDiscountCode(anyString(), eq(discount.getCode()), eq(discount.getAmount().toString()), eq("%"));
        }

        @Test
        @DisplayName("Should return false when discount not found")
        void testSendDiscount_DiscountNotFound() {
            // Mock behavior
            when(discountRepository.findById(sendDiscountRequest.getDiscountId())).thenReturn(Optional.empty());

            // When
            boolean result = discountService.sendEmailDiscounts(sendDiscountRequest);

            // Then
            assertFalse(result);
            verify(discountRepository, atMostOnce()).findById(sendDiscountRequest.getDiscountId());
            verify(emailService, never()).sendDiscountCode(anyString(), anyString(), anyString(), anyString());
        }
    }
}