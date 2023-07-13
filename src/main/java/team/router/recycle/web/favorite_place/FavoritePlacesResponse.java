package team.router.recycle.web.favorite_place;


import lombok.Builder;

import java.util.List;

@Builder
public record FavoritePlacesResponse(
        Integer count,
        List<FavoritePlaceResponse> favoritePlaces) {
}