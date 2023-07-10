package team.router.recycle.web.favorite_station;

public record FavoriteStationResponse(
        String stationName,
        Double stationLatitude,
        Double stationLongitude,
        String stationId
) {
}
