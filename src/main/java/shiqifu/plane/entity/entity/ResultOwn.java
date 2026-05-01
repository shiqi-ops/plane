package shiqifu.plane.entity.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ResultOwn {

    @SerializedName("model_path")
    private String modelPath;

    @SerializedName("dataset")
    private String dataset;

    @SerializedName("dataset_size")
    private int datasetSize;

    @SerializedName("attack")
    private String attack;

    @SerializedName("eps")
    private double eps;

    @SerializedName("clean_accuracy")
    private double cleanAccuracy;

    @SerializedName("adv_accuracy")
    private double advAccuracy;

    @SerializedName("accuracy_drop")
    private double accuracyDrop;

    @SerializedName("attack_success_rate")
    private double attackSuccessRate;

    @SerializedName("attack_time")
    private double attackTime;

    @SerializedName("robust_score")
    private double robustScore;

    @SerializedName("robust_level")
    private String robustLevel;

    @SerializedName("down_url")
    private String downloadUrl;
}
