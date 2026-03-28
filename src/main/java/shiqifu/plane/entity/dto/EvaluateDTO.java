package shiqifu.plane.entity.dto;

import lombok.Data;

@Data
public class EvaluateDTO {
    private String model;
    private String attack;
    private String dataset;
    private String eps;
}
