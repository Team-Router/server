package team.router.recycle.domain.station;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationsRealtimeResponse;

public interface StationService extends ApplicationRunner {
    @Override
    void run(ApplicationArguments args);

    @Transactional(readOnly = true)
    StationsRealtimeResponse getRealtimeStation(StationRealtimeRequest stationRealtimeRequest);

    Station findDepatureStation(Location location);

    Station findDestinationStation(Location location);

    City handledCity();
}
