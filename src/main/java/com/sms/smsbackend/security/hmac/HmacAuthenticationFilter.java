package com.sms.smsbackend.security.hmac;

import com.sms.smsbackend.entity.ApiCredential;
import com.sms.smsbackend.repository.ApiCredentialRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class HmacAuthenticationFilter extends OncePerRequestFilter {

    private final HmacUtility hmacUtility;
    private final NonceCache nonceCache;
    private final ApiCredentialRepository apiCredentialRepository;

    @Value("${hmac.auth.enabled:false}")
    private boolean hmacAuthEnabled;

    @Value("${hmac.timestamp.tolerance:300000}")
    private long timestampTolerance; // 5 minutes default

    private static final String HEADER_APP_ID = "X-App-ID";
    private static final String HEADER_TIMESTAMP = "X-App-Timestamp";
    private static final String HEADER_NONCE = "X-App-Nonce";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HMAC_PREFIX = "HMAC-SHA256 ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip if HMAC auth is disabled
        if (!hmacAuthEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap request to allow reading body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        try {
            String appId = wrappedRequest.getHeader(HEADER_APP_ID);
            String timestamp = wrappedRequest.getHeader(HEADER_TIMESTAMP);
            String nonce = wrappedRequest.getHeader(HEADER_NONCE);
            String authorization = wrappedRequest.getHeader(HEADER_AUTHORIZATION);

            // Check if this is an HMAC request
            if (!isHmacRequest(appId, timestamp, nonce, authorization)) {
                filterChain.doFilter(wrappedRequest, response);
                return;
            }

            // Extract signature
            String signature = authorization.substring(HMAC_PREFIX.length());

            // Validate timestamp
            if (!hmacUtility.validateTimestamp(timestamp, timestampTolerance)) {
                log.warn("HMAC authentication failed: Invalid timestamp");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid timestamp");
                return;
            }

            // Check nonce (prevent replay attacks)
            if (nonceCache.isNonceUsed(nonce)) {
                log.warn("HMAC authentication failed: Nonce already used (replay attack?)");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Nonce already used");
                return;
            }

            // Get API credential
            ApiCredential credential = apiCredentialRepository.findByAppId(appId)
                    .orElse(null);

            if (credential == null || !credential.getEnabled()) {
                log.warn("HMAC authentication failed: Invalid App-ID");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid App-ID");
                return;
            }

            // Read request body
            String body = getRequestBody(wrappedRequest);

            // Validate signature
            boolean isValid = hmacUtility.validateSignature(
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    timestamp,
                    nonce,
                    body,
                    signature,
                    credential.getSecretKey()
            );

            if (!isValid) {
                log.warn("HMAC authentication failed: Invalid signature");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid signature");
                return;
            }

            // Mark nonce as used
            nonceCache.markNonceAsUsed(nonce);

            // Set authentication in security context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            appId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_API"))
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(wrappedRequest));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("HMAC authentication successful for App-ID: {}", appId);

        } catch (Exception e) {
            log.error("HMAC authentication error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isHmacRequest(String appId, String timestamp, String nonce, String authorization) {
        return StringUtils.hasText(appId) &&
               StringUtils.hasText(timestamp) &&
               StringUtils.hasText(nonce) &&
               StringUtils.hasText(authorization) &&
               authorization.startsWith(HMAC_PREFIX);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) throws IOException {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "";
    }
}
