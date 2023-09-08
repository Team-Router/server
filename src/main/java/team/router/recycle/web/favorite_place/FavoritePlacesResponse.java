package team.router.recycle.web.favorite_place;


import team.router.recycle.domain.favorite_place.FavoritePlace;

import java.util.List;

public record FavoritePlacesResponse(
        Integer count,
        List<FavoritePlaceResponse> favoritePlaces) {

    public static FavoritePlacesResponse from(List<FavoritePlace> favoritePlaces) {
        return new FavoritePlacesResponse(
                favoritePlaces.size(),
                favoritePlaces.stream()
                        .map(FavoritePlaceResponse::from)
                        .toList()
        );
    }
}
