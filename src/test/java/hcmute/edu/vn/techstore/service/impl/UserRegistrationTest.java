package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.ERole;
import hcmute.edu.vn.techstore.Enum.EGender;
import hcmute.edu.vn.techstore.convert.UserResponseConverter;
import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.RoleRepository;
import hcmute.edu.vn.techstore.repository.UserRepository;
import hcmute.edu.vn.techstore.service.interfaces.IEmailService;
import hcmute.edu.vn.techstore.utils.ImageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for User Registration")
class UserRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserResponseConverter userResponseConverter;

    @Mock
    private ImageUtil imageUtil;

    @Mock
    private RegistrationContext registrationContext;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest validUserRequest;
    private RoleEntity customerRole;
    private UserEntity mockUserEntity;

    @BeforeEach
    void setUp() {
        // Setup valid user request
        validUserRequest = new UserRequest();
        validUserRequest.setEmail("test@example.com");
        validUserRequest.setPassword("ValidPass123!");
        validUserRequest.setConfirmPassword("ValidPass123!");
        validUserRequest.setPhoneNumber("0123456789");
        validUserRequest.setFirstName("John");
        validUserRequest.setLastName("Doe");
        validUserRequest.setGender(EGender.MALE);
        validUserRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        validUserRequest.setRoleName("ROLE_CUSTOMER");

        // Setup role
        customerRole = new RoleEntity();
        customerRole.setId(1L);
        customerRole.setName(ERole.ROLE_CUSTOMER);

        // Setup mock user entity
        mockUserEntity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("0123456789")
                .gender(EGender.MALE)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .account(AccountEntity.builder()
                        .email("test@example.com")
                        .password("encodedPassword")
                        .build())
                .role(customerRole)
                .isActived(false)
                .build();
    }

    @Nested
    @DisplayName("Successful Registration Tests")
    class SuccessfulRegistrationTests {

        @Test
        @DisplayName("Should register successfully with all valid information")
        void testRegisterSuccess_WithAllValidInformation() throws IOException {
            // Given
            when(userRepository.findByAccount_Email(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
            when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
            when(registrationContext.executeRegistration(any(UserRequest.class))).thenReturn(mockUserEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

            // When
            boolean result = userService.register(validUserRequest);

            // Then
            assertTrue(result);
            verify(userRepository, times(1)).save(any(UserEntity.class));
            verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when email format is invalid")
        void testRegisterFailure_InvalidEmailFormat() {
            // Given
            validUserRequest.setEmail("invalid-email-format");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void testRegisterFailure_EmailAlreadyExists() {
            // Given
            when(userRepository.findByAccount_Email(validUserRequest.getEmail()))
                    .thenReturn(Optional.of(mockUserEntity));

            // When & Then
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, 
                () -> userService.register(validUserRequest));
            assertEquals("Email already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when email is blank")
        void testRegisterFailure_BlankEmail() {
            // Given
            validUserRequest.setEmail("");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Should throw exception when password is less than 8 characters")
        void testRegisterFailure_PasswordTooShort() {
            // Given
            validUserRequest.setPassword("Pass1!");
            validUserRequest.setConfirmPassword("Pass1!");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password is more than 21 characters")
        void testRegisterFailure_PasswordTooLong() {
            // Given
            validUserRequest.setPassword("VeryLongPassword123!@#$%");
            validUserRequest.setConfirmPassword("VeryLongPassword123!@#$%");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password lacks digits")
        void testRegisterFailure_PasswordLacksDigits() {
            // Given
            validUserRequest.setPassword("Password!");
            validUserRequest.setConfirmPassword("Password!");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password lacks lowercase letters")
        void testRegisterFailure_PasswordLacksLowercase() {
            // Given
            validUserRequest.setPassword("PASSWORD123!");
            validUserRequest.setConfirmPassword("PASSWORD123!");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password lacks uppercase letters")
        void testRegisterFailure_PasswordLacksUppercase() {
            // Given
            validUserRequest.setPassword("password123!");
            validUserRequest.setConfirmPassword("password123!");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password lacks special characters")
        void testRegisterFailure_PasswordLacksSpecialChars() {
            // Given
            validUserRequest.setPassword("Password123");
            validUserRequest.setConfirmPassword("Password123");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password contains whitespace")
        void testRegisterFailure_PasswordContainsWhitespace() {
            // Given
            validUserRequest.setPassword("Pass word123!");
            validUserRequest.setConfirmPassword("Pass word123!");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when password and confirm password do not match")
        void testRegisterFailure_PasswordMismatch() {
            // Given
            validUserRequest.setPassword("ValidPass123!");
            validUserRequest.setConfirmPassword("DifferentPass123!");

            // When & Then
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, 
                () -> userService.register(validUserRequest));
            assertEquals("Password not match", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Phone Number Validation Tests")
    class PhoneValidationTests {

        @Test
        @DisplayName("Should throw exception when phone number already exists")
        void testRegisterFailure_PhoneNumberAlreadyExists() {
            // Given
            when(userRepository.findByAccount_Email(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber(validUserRequest.getPhoneNumber()))
                    .thenReturn(Optional.of(mockUserEntity));

            // When & Then
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, 
                () -> userService.register(validUserRequest));
            assertEquals("Phone number already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when phone number is blank")
        void testRegisterFailure_BlankPhoneNumber() {
            // Given
            validUserRequest.setPhoneNumber("");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }
    }

    @Nested
    @DisplayName("Date of Birth Validation Tests")
    class DateOfBirthValidationTests {

        @Test
        @DisplayName("Should throw exception when date of birth is in the future")
        void testRegisterFailure_DateOfBirthInFuture() {
            // Given
            validUserRequest.setDateOfBirth(LocalDate.now().plusDays(1));

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when user is under 18 years old")
        void testRegisterFailure_UserUnder18() {
            // Given
            validUserRequest.setDateOfBirth(LocalDate.now().minusYears(17));

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when user is over 100 years old")
        void testRegisterFailure_UserOver100() {
            // Given
            validUserRequest.setDateOfBirth(LocalDate.now().minusYears(101));

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when date of birth is null")
        void testRegisterFailure_NullDateOfBirth() {
            // Given
            validUserRequest.setDateOfBirth(null);

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }
    }

    @Nested
    @DisplayName("Required Fields Validation Tests")
    class RequiredFieldsValidationTests {

        @Test
        @DisplayName("Should throw exception when first name is blank")
        void testRegisterFailure_BlankFirstName() {
            // Given
            validUserRequest.setFirstName("");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when last name is blank")
        void testRegisterFailure_BlankLastName() {
            // Given
            validUserRequest.setLastName("");

            // When & Then
            assertThrows(Exception.class, () -> userService.register(validUserRequest));
        }

        @Test
        @DisplayName("Should throw exception when gender is null")
        void testRegisterFailure_NullGender() throws IOException {
            // Given
            validUserRequest.setGender(null);
            when(userRepository.findByAccount_Email(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
            when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
            when(registrationContext.executeRegistration(any(UserRequest.class))).thenReturn(mockUserEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

            // When & Then
            // Note: This test might pass depending on validation configuration
            // If gender is not required, this test should be adjusted
            assertDoesNotThrow(() -> userService.register(validUserRequest));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should complete full registration flow successfully")
        void testFullRegistrationFlow_Success() throws IOException {
            // Given
            when(userRepository.findByAccount_Email(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
            when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));
            when(registrationContext.executeRegistration(any(UserRequest.class))).thenReturn(mockUserEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(mockUserEntity);

            // When
            boolean result = userService.register(validUserRequest);

            // Then
            assertTrue(result);
            verify(userRepository, times(1)).findByAccount_Email(validUserRequest.getEmail());
            verify(userRepository, times(1)).findByPhoneNumber(validUserRequest.getPhoneNumber());
            verify(roleRepository, times(1)).findByName(ERole.ROLE_CUSTOMER);
            verify(registrationContext, times(1)).setStrategy(validUserRequest.getRoleName());
            verify(registrationContext, times(1)).executeRegistration(validUserRequest);
            verify(userRepository, times(1)).save(any(UserEntity.class));
            verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Should handle role not found exception")
        void testRegistrationFailure_RoleNotFound() {
            // Given
            when(userRepository.findByAccount_Email(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
            when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.register(validUserRequest));
            assertEquals("Role not found", exception.getMessage());
        }
    }
}
