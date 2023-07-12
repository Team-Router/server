package team.router.recycle.web.favorite_place;

public record FavoritePlaceResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude) {
}