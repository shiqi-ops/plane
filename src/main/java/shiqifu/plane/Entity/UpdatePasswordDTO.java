package shiqifu.plane.Entity;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String email;
    private String password;
    private String verificationCode;
}
