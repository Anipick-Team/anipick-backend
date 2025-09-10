package com.anipick.backend.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class RealTimeRankWrapper {
    @JsonProperty("real_time_rank")
    List<Long> realTimeRank;
}
