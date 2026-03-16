package shiqifu.plane.Entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EvaluateOwnDTO {
    private String model_pth;
    private String attack;
    private String dataset;
    private String eps;
    private MultipartFile model_file;
}
