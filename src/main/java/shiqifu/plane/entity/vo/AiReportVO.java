package shiqifu.plane.entity.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiReportVO {

    @JsonProperty("core_conclusions")
    @JsonAlias({"coreConclusions", "core_conclusions"})
    private String coreConclusions;

    @JsonProperty("weakness_analysis")
    @JsonAlias({"weaknessAnalysis", "weakness_analysis"})
    private String weaknessAnalysis;

    @JsonProperty("optimization_suggestions")
    @JsonAlias({"optimizationSuggestions", "optimization_suggestions"})
    private String optimizationSuggestions;
}