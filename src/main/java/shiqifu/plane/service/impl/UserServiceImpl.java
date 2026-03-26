package shiqifu.plane.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shiqifu.plane.Entity.UpdatePasswordDTO;
import shiqifu.plane.Entity.User;
import shiqifu.plane.exception.VerificationCodeErrorException;
import shiqifu.plane.exception.VerificationCodeNullException;
import shiqifu.plane.mapper.UserMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private RedisTemplate redisTemplate;
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
    public void send(String email){
        String id= "email_id";
        String key=id+email;
        String value=String.format("%06d",new Random().nextInt(1000000));
        redisTemplate.opsForValue().set(key, value,5, TimeUnit.MINUTES);
        send("3181842264@qq.com",email,"邮箱验证码", "您的验证码是：" + value + "，5分钟内有效。");
    }
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) throws VerificationCodeNullException, VerificationCodeErrorException {
        String id="email_id";
        String email=updatePasswordDTO.getEmail();
        String key=id+email;
        String value=(String) redisTemplate.opsForValue().get(key);
        String verificationCode=updatePasswordDTO.getVerificationCode();
        if(value==null||verificationCode==null){
            throw new VerificationCodeNullException("验证码为空");
        }
        redisTemplate.delete(key);
        if(!verificationCode.equals(value)){
            throw new VerificationCodeErrorException("验证码不正确");
        }
        String password=passwordEncoder.encode(updatePasswordDTO.getPassword());
        userMapper.updateByEmail(email,password);
    }
    private void send(String from, String to,String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            sender.send(message);
            System.out.println("发送成功");
        }
       catch (Exception e) {
           System.err.println("邮件发送失败: " + e.getMessage());
           throw new RuntimeException(e);
       }
    }
}
