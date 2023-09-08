package team.router.recycle.web.favorite_station;

import team.router.recycle.domain.station.Station;

public record FavoriteStationResponse(
        String name,
        Double latitude,
        Double longitude,
        String id
) {
    public static FavoriteStationResponse from(Station station) {
        return new FavoriteStationResponse(
                station.getStationName(),
                station.getStationLatitude(),
                station.getStationLongitude(),
                station.getStationId()
        );
    }
}
