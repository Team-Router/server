package team.router.recycle.domain.favorite_place;


import java.util.List;

public record FavoritePlacesResponse(
        Integer count,
        List<FavoritePlaceResponse> favoritePlaces) {
}