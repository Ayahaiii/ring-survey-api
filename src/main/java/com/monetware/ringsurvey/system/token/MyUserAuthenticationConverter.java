package com.monetware.ringsurvey.system.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义返回用户信息
 * @author Simo
 * @date 2019-08-08
 */
public class MyUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    private Collection<? extends GrantedAuthority> defaultAuthorities;

    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey("user_name")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("user_name", map.get("userInfo"));
            Collection<? extends GrantedAuthority> authorities = this.getAuthorities(map);
            return new UsernamePasswordAuthenticationToken(resMap, "N/A", authorities);
        } else {
            return null;
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return defaultAuthorities;
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");

    }


}
