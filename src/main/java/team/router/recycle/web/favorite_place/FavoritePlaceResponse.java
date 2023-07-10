package team.router.recycle.web.favorite_place;

public record FavoritePlaceResponse(
        Long id,
        String placeName,
        Double latitude,
        Double longitude) {
}