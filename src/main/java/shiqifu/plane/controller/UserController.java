package shiqifu.plane.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import shiqifu.plane.entity.dto.UpdateDTO;
import shiqifu.plane.entity.dto.UpdateEmailDTO;
import shiqifu.plane.entity.dto.UpdatePasswordDTO;
import shiqifu.plane.entity.entity.SendDTO;
import shiqifu.plane.entity.entity.User;
import shiqifu.plane.entity.dto.UserLoginDTO;
import shiqifu.plane.exception.PasswordErrorException;
import shiqifu.plane.exception.UserNotFoundException;
import shiqifu.plane.exception.VerificationCodeErrorException;
import shiqifu.plane.exception.VerificationCodeNullException;
import shiqifu.plane.service.impl.UserServiceImpl;
import shiqifu.plane.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody UserLoginDTO userLoginDTO) throws PasswordErrorException {
        log.info("登录");
        String username=userLoginDTO.getUsername();
        String password=userLoginDTO.getPassword();
        UserDetails userDetails;
        try{
            userDetails=userService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new PasswordErrorException("用户名或密码错误");
        }
        Map<String,Object> claims=new HashMap<>();
        claims.put("role","admin");
        String token=jwtUtil.generateToken(username,claims);
        Map<String,String> map=new HashMap<>();
        map.put("token",token);
        map.put("username",username);
        return map;
    }
    @PostMapping("/register")
    public void register(@RequestBody User user){
        userService.save(user);
    }

    @PostMapping("/send")
    public void send(@RequestBody SendDTO sendDTO){
        log.info("发送验证码");
        String email=sendDTO.getEmail();
        userService.send(email);
    }
    @PostMapping("/update")
    public void update(@RequestBody UpdatePasswordDTO updatePasswordDTO) throws VerificationCodeErrorException, VerificationCodeNullException {
        log.info("修改密码");
        userService.updatePassword(updatePasswordDTO);
    }
    @PostMapping("/update_password")
    public void update_password(@RequestBody UpdateDTO updateDTO) throws UserNotFoundException {
        log.info("修改密码2.0");
        userService.update(updateDTO);
    }
    @PostMapping("/update_email")
    public void update_email(@RequestBody UpdateEmailDTO updateEmailDTO) throws UserNotFoundException {
        log.info("修改邮箱");
        userService.updateEmail(updateEmailDTO);
    }
}
