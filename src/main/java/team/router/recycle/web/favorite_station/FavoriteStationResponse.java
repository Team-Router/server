package team.router.recycle.web.favorite_station;

public record FavoriteStationResponse(
        String name,
        Double latitude,
        Double longitude,
        String id
) {
}
