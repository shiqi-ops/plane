package shiqifu.plane.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shiqifu.plane.Entity.User;
import shiqifu.plane.mapper.UserMapper;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userMapper.findByUsername(username);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        User user1 = user.get();
        return new org.springframework.security.core.userdetails.User(
                user1.getUsername(),
                user1.getPassword(), // 数据库中必须是 BCrypt 加密后的密码
                user1.isEnabled(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(user1.getRole()))
        );
    }
    public void save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        userMapper.save(user);
    }
}
