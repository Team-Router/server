package team.router.recycle.domain.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RouteRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CycleRequest {
        private String startLatitude;
        private String startLongitude;
        private String endLatitude;
        private String endLongitude;
    }
}
