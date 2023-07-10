package team.router.recycle.domain.favorite_place;

public record FavoritePlaceResponse(
        Long id,
        String placeName,
        Double latitude,
        Double longitude) {
}