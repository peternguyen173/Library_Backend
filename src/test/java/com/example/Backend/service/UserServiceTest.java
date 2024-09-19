package com.example.Backend.service;

import com.example.Backend.config.secutiry.JwtTokenProvider;
import com.example.Backend.entity.CustomUserDetails;
import com.example.Backend.entity.User;
import com.example.Backend.entity.enumModel.UserStatus;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.request.AuthRequest;
import com.example.Backend.request.UserRequest;
import com.example.Backend.response.AuthResponse;
import com.example.Backend.service.BlacklistService;
import com.example.Backend.service.EmailService;
import com.example.Backend.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private BlacklistService blacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnUserDetails() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
    }

    @Test
    void findUserByJwt_ValidToken_ReturnUser() throws Exception {
        String email = "test@example.com";
        String token = "validJwtToken";
        User mockUser = new User();
        mockUser.setEmail(email);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        User user = userService.findUserByJwt(token);

        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    void signIn_ValidCredentials_ReturnAuthResponse() throws BadRequestException {
        String email = "test@example.com";
        String password = "password";
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(password);
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setRole("ROLE_USER");
        mockUser.setPassword(passwordEncoder.encode(password));
        mockUser.setStatus(UserStatus.VERIFIED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        AuthResponse authResponse = userService.signIn(authRequest);

        assertNotNull(authResponse);
        assertTrue(authResponse.isStatus());
        assertNotNull(authResponse.getJwt());
        assertEquals(email, authResponse.getEmail());
        verify(userRepository, atLeast(1)).findByEmail(email); // or use times(2) if it should be called twice
    }

    @Test
    void createUser_ValidUser_CreatesUser() throws Exception {
        UserRequest userRequest = new UserRequest("newuser@example.com", "password", "name", "address", "123456789");
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        AuthResponse authResponse = userService.createUser(userRequest);

        assertNotNull(authResponse);
        assertTrue(authResponse.isStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_EmailExists_ThrowsBadRequestException() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("existing@example.com");
        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void changePassword_ValidJwt_ChangesPassword() throws Exception {
        String jwt = "validJwt";
        String newPassword = "newPassword";
        User mockUser = new User();
        when(jwtTokenProvider.getEmailFromToken(jwt)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User updatedUser = userService.changePassword(jwt, newPassword);

        assertNotNull(updatedUser);
        verify(userRepository, times(1)).save(mockUser);
        verify(passwordEncoder, times(1)).encode(newPassword);
    }
}
