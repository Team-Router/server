package team.router.recycle.domain.station;


import org.springframework.stereotype.Service;
import team.router.recycle.web.station.StationRealtimeRequest;

@Service
public class StationServiceFactory {

    private final SeoulStationService seoulStationService;
    private final DaejeonStationService daejeonStationService;

    public StationServiceFactory(SeoulStationService seoulStationService, DaejeonStationService daejeonStationService) {
        this.seoulStationService = seoulStationService;
        this.daejeonStationService = daejeonStationService;
    }

    public StationService getStationService(StationRealtimeRequest stationRealtimeRequest) {
        if (stationRealtimeRequest.getCity().equals("seoul")) {
            return seoulStationService;
        }
        return daejeonStationService;
    }
}
