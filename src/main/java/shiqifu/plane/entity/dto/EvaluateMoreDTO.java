package shiqifu.plane.entity.dto;

import lombok.Data;

@Data
public class EvaluateMoreDTO {
    private String model;
    private String attack_group;
    private String dataset;
    private String eps;
}
