package shiqifu.plane.mapper;

import org.springframework.data.jpa.repository.JpaRepository;
import shiqifu.plane.Entity.User;

import java.util.Optional;

public interface UserMapper extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

}
