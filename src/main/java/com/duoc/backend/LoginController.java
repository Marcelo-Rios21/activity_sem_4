package com.duoc.backend;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController {

    private final JWTAuthenticationConfig jwtAuthtenticationConfig;
    private final MyUserDetailsService userDetailsService;

    public LoginController(
            JWTAuthenticationConfig jwtAuthtenticationConfig,
            MyUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.jwtAuthtenticationConfig = jwtAuthtenticationConfig;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("login")
    public String login(
            @RequestParam("user") String username,
            @RequestParam("encryptedPass") String encryptedPass) {

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!userDetails.getPassword().equals(encryptedPass)) {
            throw new RuntimeException("Invalid login");
        }

        String token = jwtAuthtenticationConfig.getJWTToken(username);
        return token;
    }
}