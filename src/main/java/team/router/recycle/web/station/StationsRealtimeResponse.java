package team.router.recycle.web.station;

import lombok.Builder;

import java.util.List;

@Builder
public record StationsRealtimeResponse(
        Integer count,
        List<StationRealtimeResponse> stationRealtimeResponses
) {
}