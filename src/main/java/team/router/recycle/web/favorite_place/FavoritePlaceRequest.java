package team.router.recycle.web.favorite_place;

import team.router.recycle.domain.favorite_place.FavoritePlace.Type;

public record FavoritePlaceRequest(Double longitude, Double latitude, Type type) {
}