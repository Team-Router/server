package team.router.recycle.web.station;

import java.util.List;

public record StationsRealtimeResponse(
        Integer count,
        List<StationRealtimeResponse> stationRealtimeResponses
) {
    public static StationsRealtimeResponse from(List<StationRealtimeResponse> stationList) {
        return new StationsRealtimeResponse(stationList.size(), stationList);
    }
}
