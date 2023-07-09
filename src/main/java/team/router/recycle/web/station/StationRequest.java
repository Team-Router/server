package team.router.recycle.web.station;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class StationRequest {
    
    @Getter
    @NoArgsConstructor
    public static class RealtimeStationRequest {
        private Double latitude;
        private Double longitude;
    }
}
