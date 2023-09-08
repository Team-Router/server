package team.router.recycle.domain.station;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Primary
public class StationRedisServiceImpl implements StationRedisService {

    private final RedisTemplate<String, Station> redisTemplate;

    public StationRedisServiceImpl(RedisTemplate<String, Station> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isValid(String stationId) {
        return redisTemplate.hasKey(stationId);
    }

    @Override
    public boolean isInvalid(String stationId) {
        return !isValid(stationId);
    }
}
