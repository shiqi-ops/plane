package shiqifu.plane.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "plane")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
    private boolean enabled;

    private String email;
}
