package shiqifu.plane.entity.vo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AgentVO {
    @SerializedName("risk_score")
    private Double riskScore;

    @SerializedName("risk_level")
    private String riskLevel;

    @SerializedName("is_suspicious")
    private Boolean isSuspicious;

    @SerializedName("confidence_shift")
    private Double confidenceShift;

    @SerializedName("original_label")
    private Integer originalLabel;

    @SerializedName("squeezed_label")
    private Integer squeezedLabel;

    @SerializedName("method")
    private String method;
}
