package team.router.recycle.web.station;

import com.fasterxml.jackson.annotation.JsonGetter;

public record StationRealtimeResponse(
        @JsonGetter("name")
        String stationName,
        @JsonGetter("count")
        Integer parkingBikeTotCnt,
        @JsonGetter("latitude")
        Double stationLatitude,
        @JsonGetter("longitude")
        Double stationLongitude,
        @JsonGetter("id")
        String stationId
) {
}
