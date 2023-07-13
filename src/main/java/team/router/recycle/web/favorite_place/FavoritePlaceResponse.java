package team.router.recycle.web.favorite_place;

import lombok.Builder;

@Builder
public record FavoritePlaceResponse(
        Long id,
        String name,
        Double latitude,
        Double longitude) {
}