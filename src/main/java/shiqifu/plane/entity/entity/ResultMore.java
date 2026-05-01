package shiqifu.plane.entity.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ResultMore {
    @SerializedName("model")
    private String model;

    @SerializedName("dataset")
    private String dataset;

    @SerializedName("dataset_size")
    private Integer datasetSize;

    @SerializedName("clean_accuracy")
    private Double cleanAccuracy;

    @SerializedName("attack_results")
    private List<AttackResult> attackResults;

    @SerializedName("ranking")
    private List<AttackResult> ranking;

    @SerializedName("robust_score")
    private Double robustScore;

    @SerializedName("robust_level")
    private String robustLevel;

    @SerializedName("bubble_data")
    private List<Bubble> bubbleData;

    @SerializedName("robustness_curve")
    private String robustnessCurve;

    @SerializedName("attack_bar")
    private String attackBar;

    @SerializedName("attack_heatmap")
    private String attackHeatmap;

    @SerializedName("attack_bubble")
    private String attackBubble;

    @SerializedName("download_url")
    @JsonProperty("download_url")
    private String downloadUrl;
}
