package com.solo.bulletinboard.auth.filter;

import com.solo.bulletinboard.auth.jwt.JwtTokenizer;
import com.solo.bulletinboard.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils customAuthorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, CustomAuthorityUtils customAuthorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.customAuthorityUtils = customAuthorityUtils;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String accessToken = request.getHeader("Authorization");
        return accessToken == null || !accessToken.startsWith("Bearer");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        }
        catch (SignatureException se){
            request.setAttribute("SignatureException", se);
        }
        catch (ExpiredJwtException ee){
            request.setAttribute("ExpiredJwtException", ee);
        }
        catch (Exception e){
            request.setAttribute("Exception", e);
        }

        filterChain.doFilter(request, response);

    }

    private Map<String, Object> verifyJws(HttpServletRequest request){

        String jws = request.getHeader("Authorization").replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }

    private void setAuthenticationToContext(Map<String, Object> claims){
        String username = (String) claims.get("username");
        List<String> roles = (List) claims.get("roles");

        List<GrantedAuthority> authorities = customAuthorityUtils.createAuthorities(roles);

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
