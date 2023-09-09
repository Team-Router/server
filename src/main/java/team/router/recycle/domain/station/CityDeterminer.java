package team.router.recycle.domain.station;

import team.router.recycle.domain.route.model.Location;

public final class CityDeterminer {
    public static boolean isSameCity(Location startLocation, Location endLocation) {
        return determineCity(startLocation).equals(determineCity(endLocation));
    }

    public static City determineCity(Double latitude, Double longitude) {
        // Seoul의 범위에 속하는지 확인
        if (latitude > 37 && latitude < 38 && longitude > 126 && longitude < 127) {
            return City.SEOUL;
        }

        // Daejeon의 범위에 속하는지 확인
        if (latitude > 36.1643402 && latitude < 36.4969715 && longitude > 127.2701417 && longitude < 127.6108633) {
            return City.DAEJEON;
        }

        // 기타 조건들...

        // 기본적으로 Seoul 반환 (또는 선택 사항에 따라 null 반환 또는 예외 발생 시킴)
        return City.SEOUL;
    }

    public static City determineCity(Location location) {
        return determineCity(location.latitude(), location.longitude());
    }
}
