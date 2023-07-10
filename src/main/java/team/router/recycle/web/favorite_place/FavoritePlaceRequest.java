package team.router.recycle.web.favorite_place;

import team.router.recycle.domain.favorite_place.FavoritePlace.Type;

public record FavoritePlaceRequest(AddFavoritePlace addFavoritePlace) {

    public record AddFavoritePlace(Double longitude, Double latitude, Type type) {
    }
}