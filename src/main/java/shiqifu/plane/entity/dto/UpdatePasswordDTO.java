package shiqifu.plane.entity.dto;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String email;
    private String password;
    private String verificationCode;
}
