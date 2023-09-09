package team.router.recycle.domain.station;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StationRedisService {

    private final RedisTemplate<String, Station> redisTemplate;

    public StationRedisService(RedisTemplate<String, Station> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void isValid(String stationId) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(stationId))) {
            throw new RecycleException(ErrorCode.STATION_NOT_FOUND, "존재하지 않는 대여소입니다.");
        }
    }

    public void multiSet(Map<String, Station> stationMap) {
        redisTemplate.opsForValue().multiSet(stationMap);
    }

    public List<Station> multiGet() {
        return redisTemplate.opsForValue().multiGet(Objects.requireNonNull(redisTemplate.keys("*")));
    }
}
