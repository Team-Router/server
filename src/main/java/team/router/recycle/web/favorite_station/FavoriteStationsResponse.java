package team.router.recycle.web.favorite_station;

import java.util.List;

public record FavoriteStationsResponse(
        Integer count,
        List<FavoriteStationResponse> favoriteStationResponses
) {
}
