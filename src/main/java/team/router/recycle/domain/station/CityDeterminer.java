package team.router.recycle.domain.station;

import org.springframework.stereotype.Service;
import team.router.recycle.domain.route.model.Location;

@Service
public class CityDeterminer {
    public boolean isSameCity(Location startLocation, Location endLocation) {
        return determineCity(startLocation).equals(determineCity(endLocation));
    }

    public City determineCity(Double latitude, Double longitude) {
        // Seoul의 범위에 속하는지 확인
        if (latitude > 37 && latitude < 38 && longitude > 126 && longitude < 127) {
            return City.SEOUL;
        }

        // Daejeon의 범위에 속하는지 확인
        if (latitude > 36 && latitude < 37 && longitude > 127 && longitude < 128) {
            return City.DAEJEON;
        }

        // 기타 조건들...

        // 기본적으로 Seoul 반환 (또는 선택 사항에 따라 null 반환 또는 예외 발생 시킴)
        return City.SEOUL;
    }

    public City determineCity(Location location) {
        return determineCity(location.latitude(), location.longitude());
    }
}
