package team.router.recycle.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

@Order(1)
@Component
public class DatabaseUtil implements ApplicationRunner {

    private final RedisTemplate<String, Station> redisTemplate;
    private final StationRepository stationRepository;

    public DatabaseUtil(RedisTemplate<String, Station> redisTemplate, StationRepository stationRepository) {
        this.redisTemplate = redisTemplate;
        this.stationRepository = stationRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        stationRepository.truncate();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
