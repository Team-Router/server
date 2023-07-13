package team.router.recycle.web.favorite_station;

import lombok.Builder;

@Builder
public record FavoriteStationResponse(
        String name,
        Double latitude,
        Double longitude,
        String id
) {
}
