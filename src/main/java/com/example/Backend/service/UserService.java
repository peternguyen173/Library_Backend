package com.example.Backend.service;

import com.example.Backend.config.secutiry.JwtTokenProvider;
import com.example.Backend.entity.CustomUserDetails;
import com.example.Backend.entity.User;
import com.example.Backend.entity.enumModel.ERole;
import com.example.Backend.entity.enumModel.UserStatus;
import com.example.Backend.exception.BadRequestException;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.request.AuthRequest;
import com.example.Backend.request.UserRequest;
import com.example.Backend.response.AuthResponse;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BlacklistService blacklistService;

    @Value("${server.port:8080}") // Sẽ sử dụng cổng 8080 nếu không có cấu hình
    private String port;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user);
    }

    public User findUserByJwt(String jwt) throws Exception {
        String email = jwtTokenProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("USER NOT FOUND"));
        return user;
    }

    public AuthResponse signIn(AuthRequest authRequest) throws BadCredentialsException, BadRequestException {
        AuthResponse authResponse = new AuthResponse();
        String username = authRequest.getEmail();
        String password = authRequest.getPassword();
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);
        authResponse.setStatus(true);
        authResponse.setJwt(accessToken);
        authResponse.setEmail(username);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        authResponse.setName(userDetails.getUser().getName());
        return authResponse;
    }

    public AuthResponse signInAdmin(AuthRequest authRequest) throws BadCredentialsException, BadRequestException {
        AuthResponse authResponse = new AuthResponse();
        String username = authRequest.getEmail();
        String password = authRequest.getPassword();
        Authentication authentication = authenticateAdmin(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(userDetails.getUser().getRole().equals("ROLE_ADMIN")) {
            String accessToken = jwtTokenProvider.generateToken(authentication);
            authResponse.setStatus(true);
            authResponse.setJwt(accessToken);
            authResponse.setEmail(username);
            authResponse.setName(userDetails.getUser().getName());
            return authResponse;
        }
        else{
            authResponse.setStatus(true);
            authResponse.setJwt("Not found admin!");
            authResponse.setEmail(username);
        }
            return authResponse;
    }

    private Authentication authenticate(String username, String password) throws BadRequestException {
        UserDetails userDetails = loadUserByUsername(username);
        System.out.println(userDetails);
        if(userDetails == null) {
            throw new BadCredentialsException("Username not exist");
        }
        if(! (passwordEncoder.matches(password, userDetails.getPassword()))) {
            throw new BadCredentialsException("Wrong password");
        }
        User user = userRepository.findByEmail(username).get();
        if(user.getStatus() != UserStatus.VERIFIED) {
            throw new BadRequestException("This account is unverified");
        }
        if(user.getRole().equals("ROLE_ADMIN")) {
            throw new BadRequestException("This account is not for a user");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    private Authentication authenticateAdmin(String username, String password) throws BadRequestException {
        UserDetails userDetails = loadUserByUsername(username);
        System.out.println(userDetails);
        if(userDetails == null) {
            throw new BadCredentialsException("Admin not exist");
        }
        if(! (passwordEncoder.matches(password, userDetails.getPassword()))) {
            throw new BadCredentialsException("Wrong password");
        }
        User user = userRepository.findByEmail(username).get();
        if(user.getStatus() != UserStatus.VERIFIED) {
            throw new BadRequestException("This account is unverified");
        }
        if(!user.getRole().equals("ROLE_ADMIN")) {
            throw new BadRequestException("Wrong admin login information");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Transactional
    public AuthResponse createUser(UserRequest user) throws BadCredentialsException, BadRequestException, MessagingException {
            String email = user.getEmail();
            String password = user.getPassword();
            String role = ERole.ROLE_USER.toString();
            String verificationToken = UUID.randomUUID().toString();
        if(userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(user.getName());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setAddress(user.getAddress());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setRole(role);
        newUser.setVerificationToken(verificationToken);
        newUser.setStatus(UserStatus.UNVERIFIED);
        newUser = userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Context context = new Context();
        String url = "http://localhost:" + port + "/api/auth/verify-email/" + verificationToken;
        context.setVariable("url", url);
        emailService.sendEmailWithHtmlTemplate(email, "\u0058\u00e1\u0063\u0020\u006e\u0068\u1ead\u006e\u0020\u0111\u1ecb\u0061\u0020\u0063\u0068\u1ec9\u0020\u0065\u006d\u0061\u0069\u006c\u0020\u0063\u1ee7\u0061\u0020\u0062\u1ea1\u006e", "email-verification", context);
        return new AuthResponse(null, true);
    }

    public User changePassword(String jwt, String newPassword) throws Exception {
        User user = findUserByJwt(jwt);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public void logout(String jwt) {
        blacklistService.addToBlacklist(jwt);
    }


    public String confirmEmail(String token) throws Exception {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new NotFoundException("User not found!"));
        if(!token.equals(user.getVerificationToken())){
            userRepository.delete(user);
            throw new BadRequestException("Verification Token invalid");
        }
        user.setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
        return "Email verification successful!";
    }
}
