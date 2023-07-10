package team.router.recycle.web.favorite_place;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.router.recycle.domain.favorite_place.FavoritePlace.Type;

public class FavoritePlaceRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AddFavoritePlace{
        private Double longitude;
        private Double latitude;
        private Type type;
    }
}
