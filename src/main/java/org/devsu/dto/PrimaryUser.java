package org.devsu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.devsu.entity.Client;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class PrimaryUser implements UserDetails {

    private String name;
    private String username;
    private String password;
    private Map<String, Object> properties;
    private Collection<? extends GrantedAuthority> authorities;

    public static PrimaryUser build(Client client, Map<String, Object> properties) {
        List<GrantedAuthority> authorities = client
                .getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new PrimaryUser(client.getName(),
                client.getUsername(),
                client.getPassword(),
                properties,
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
