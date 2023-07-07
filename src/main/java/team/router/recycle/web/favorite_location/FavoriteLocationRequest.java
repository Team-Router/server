package team.router.recycle.web.favorite_location;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteLocationRequest {
    
    private String name;
    private Double latitude;
    private Double longitude;
}
