package team.router.recycle.web.favorite_station;

import lombok.Builder;

import java.util.List;

@Builder
public record FavoriteStationsResponse(
        Integer count,
        List<FavoriteStationResponse> favoriteStationResponses
) {
}
