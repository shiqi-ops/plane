package shiqifu.plane.entity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String model;

    private String dataset;

    private String attack;

    private double eps;

    @SerializedName("clean_accuracy")
    @JsonProperty("clean_accuracy")
    private double cleanAccuracy;

    @SerializedName("adv_accuracy")
    @JsonProperty("adv_accuracy")
    private double advAccuracy;

    @SerializedName("accuracy_drop")
    @JsonProperty("accuracy_drop")
    private double accuracyDrop;


    @SerializedName("robust_level")
    @JsonProperty("robust_level")
    private String robustLevel;

    @SerializedName("robust_score")
    @JsonProperty("robust_score")
    private double robustScore;

    @SerializedName("curve_path")
    @JsonProperty("curve_path")
    private String curvePath;

    @SerializedName("compare_path")
    @JsonProperty("compare_path")
    private String comparePath;

    @SerializedName("download_url")
    @JsonProperty("download_url")
    private String downloadUrl;
}
