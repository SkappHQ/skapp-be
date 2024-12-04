package com.rootcode.skapp.timeplanner.payload.request;

import com.rootcode.skapp.timeplanner.type.TrendPeriod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LateArrivalTrendFilterDto {

	private List<Long> teams;

	private TrendPeriod trendPeriod;

}
