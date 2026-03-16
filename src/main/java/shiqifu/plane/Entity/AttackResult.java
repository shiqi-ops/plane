package shiqifu.plane.Entity;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AttackResult {
    @SerializedName("attack")
    private String attack;

    @SerializedName("clean_accuracy")
    private Double cleanAccuracy;

    @SerializedName("adv_accuracy")
    private Double advAccuracy;

    @SerializedName("accuracy_drop")
    private Double accuracyDrop;

    @SerializedName("attack_success_rate")
    private Double attackSuccessRate;
}
