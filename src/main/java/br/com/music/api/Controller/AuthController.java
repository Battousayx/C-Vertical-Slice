package br.com.music.api.Controller;

import br.com.music.api.Controller.dto.JwtAuthResponse;
import br.com.music.api.Controller.dto.LoginRequest;
import br.com.music.api.Controller.dto.LogoutResponse;
import br.com.music.api.Config.JwtTokenProvider;
import br.com.music.api.Domain.User;
import br.com.music.api.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for JWT token generation")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                         UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthResponse(jwt, loginRequest.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest registrationRequest) {
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        User user = new User(
                registrationRequest.getUsername(),
                passwordEncoder.encode(registrationRequest.getPassword()),
                registrationRequest.getUsername() + "@example.com"
        );

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully. Use login endpoint to get JWT token");
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client-side token removal)")
    public ResponseEntity<?> logoutUser() {
        String username = "unknown";
        try {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (authentication != null) {
                username = authentication.getName();
            }
        } catch (Exception e) {
            // Continue with default username
        }
        return ResponseEntity.ok(new LogoutResponse("Logged out successfully", username));
    }
}
