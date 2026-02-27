package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.repository.IUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository IUserRepository;

    public UserDetailsServiceImpl(IUserRepository IUserRepository) {
        this.IUserRepository = IUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = IUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getMasterPasswordHash(),
                user.isEmailVerified(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                !user.isAccountLocked(), // accountNonLocked
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
