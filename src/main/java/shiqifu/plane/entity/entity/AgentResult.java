package shiqifu.plane.entity.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResult {
    @SerializedName("risk_score")
    private Double risk_score;
    @SerializedName("risk_level")
    private String risk_level;
    @SerializedName("agent1_analysis")
    private String agent1_analysis;
    @SerializedName("agent2_analysis")
    private String agent2_analysis;
    @SerializedName("final_report")
    private String final_report;
}
