package team.router.recycle.web.favorite_place;

import team.router.recycle.domain.favorite_place.FavoritePlace;

public record FavoritePlaceResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude) {

    public static FavoritePlaceResponse from(FavoritePlace favoritePlace) {
        return new FavoritePlaceResponse(
                favoritePlace.getId(),
                favoritePlace.getName(),
                favoritePlace.getLatitude(),
                favoritePlace.getLongitude()
        );
    }
}
