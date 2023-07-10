package team.router.recycle.config;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import team.router.recycle.domain.station.StationService;

@Component
@RequiredArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final StationService stationService;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        stationService.initStation();
    }
}
