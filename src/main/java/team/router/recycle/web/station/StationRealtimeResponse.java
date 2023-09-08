package team.router.recycle.web.station;

import com.fasterxml.jackson.annotation.JsonAlias;

public record StationRealtimeResponse(
        @JsonAlias({"stationName", "name"})
        String name,
        @JsonAlias({"parkingBikeTotCnt", "parking_count"})
        Integer count,
        @JsonAlias({"stationLatitude", "x_pos"})
        Double latitude,
        @JsonAlias({"stationLongitude", "y_pos"})
        Double longitude,
        @JsonAlias({"stationId", "id"})
        String id
) {
}
