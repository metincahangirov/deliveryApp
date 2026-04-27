package com.example.notificationservice_ms.auth;

import com.example.notificationservice_ms.config.AppJwtProperties;
import com.example.notificationservice_ms.config.AppSecurityProperties;
import com.example.notificationservice_ms.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final JwtService jwtService;
    private final AppJwtProperties jwtProperties;
    private final AppSecurityProperties securityProperties;

    public AuthController(
            JwtService jwtService,
            AppJwtProperties jwtProperties,
            AppSecurityProperties securityProperties
    ) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.securityProperties = securityProperties;
    }

    /**
     * JWT yaradır. {@code app.security.internal-token} boş deyilsə, eyni dəyərlə
     * {@value #INTERNAL_TOKEN_HEADER} header mütləqdir (daxili xidmətlər / gateway).
     */
    @PostMapping("/token")
    public TokenResponse issue(HttpServletRequest httpRequest, @Valid @RequestBody IssueTokenRequest request) {
        String expected = securityProperties.internalToken();
        if (StringUtils.hasText(expected)) {
            String provided = httpRequest.getHeader(INTERNAL_TOKEN_HEADER);
            if (!expected.equals(provided)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Etibarsız və ya çatışmayan " + INTERNAL_TOKEN_HEADER + " header."
                );
            }
        }

        String token = jwtService.createAccessToken(request.userId());
        return new TokenResponse(token, "Bearer", jwtProperties.expirationMs());
    }
}
