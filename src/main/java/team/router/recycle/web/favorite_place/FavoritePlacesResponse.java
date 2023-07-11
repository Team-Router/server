package team.router.recycle.web.favorite_place;


import java.util.List;

public record FavoritePlacesResponse(
        Integer count,
        List<FavoritePlaceResponse> favoritePlaces) {
}