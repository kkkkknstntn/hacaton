package com.spl.chatservice.config;

import com.spl.chatservice.entity.UserEntity;
import com.spl.chatservice.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity userEntity =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("user " + username + " not found"));

    Set<GrantedAuthority> authorities =
        userEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toSet());

    return UserDetailsImpl.builder()
        .username(userEntity.getUsername())
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .enabled(true)
        .password(userEntity.getPassword())
        .authorities(authorities)
        .id(userEntity.getId())
        .build();
  }
}
