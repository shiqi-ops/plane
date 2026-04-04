package shiqifu.plane.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import shiqifu.plane.entity.entity.User;

import java.util.Optional;

public interface UserMapper extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("update User u set u.password=:password where u.email=:email")
    void updateByEmail(String email,String password);

    @Query("update User u set u.password=:newPassword where u.username=:username")
    @Transactional
    @Modifying
    void updatePassword(String username,String newPassword);

    @Query("update User u set u.email=:newEmail where u.username=:username")
    @Transactional
    @Modifying
    void updateEmail(String username,String newEmail);
}
