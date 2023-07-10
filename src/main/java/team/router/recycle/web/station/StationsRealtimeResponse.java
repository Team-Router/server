package team.router.recycle.web.station;

import java.util.List;

public record StationsRealtimeResponse(
        Integer count,
        List<StationRealtimeResponse> data
) {
}