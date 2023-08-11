package team.router.recycle.domain.station;


public interface StationRedisService {

    boolean isValid(String stationId);

    boolean isInvalid(String stationId);
}
