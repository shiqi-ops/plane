package shiqifu.plane.entity.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Bubble {
    @SerializedName("attack")
    private String attack;
    @SerializedName("query_time")
    private Double queryTime;
    @SerializedName("success_rate")
    private Double successRate;
    @SerializedName("eps")
    private Double eps;
}
