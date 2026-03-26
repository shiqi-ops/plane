package shiqifu.plane.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import shiqifu.plane.Entity.User;

import java.util.Optional;

public interface UserMapper extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("update User u set u.password=:password where u.email=:email")
    void updateByEmail(String email,String password);
}
