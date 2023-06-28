package team.router.recycle.domain.route;

import org.springframework.stereotype.Service;
import team.router.recycle.domain.station.StationRepository;

@Service
public class RouteService {


    private final RouteClient routeClient;
    private final StationRepository stationRepository;

}
