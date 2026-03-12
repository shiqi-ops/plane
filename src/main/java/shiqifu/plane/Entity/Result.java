package shiqifu.plane.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    @JsonProperty("model")
    private String model;

    @JsonProperty("dataset")
    private String dataset;

    @JsonProperty("attack")
    private String attack;

    @JsonProperty("eps")
    private Double eps;

    @JsonProperty("clean accuracy")
    private Double cleanAccuracy;

    @JsonProperty("adv accuracy")
    private Double advAccuracy;

    @JsonProperty("accuracy drop")
    private Double accuracyDrop;

    @JsonProperty("robust score")
    private Double robustScore;

    @JsonProperty("robust level")
    private String robustLevel;

    @JsonProperty("curve path")
    private String curvePath;

    @JsonProperty("compare path")
    private String comparePath;
}
