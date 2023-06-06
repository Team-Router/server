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

        public CycleRequest(String startLatitude, String startLongitude, String endLatitude, String endLongitude) {
            this.startLatitude = startLatitude;
            this.startLongitude = startLongitude;
            this.endLatitude = endLatitude;
            this.endLongitude = endLongitude;
        }
    }
}
