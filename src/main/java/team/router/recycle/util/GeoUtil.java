package team.router.recycle.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GeoUtil {
    // 지구의 반지름 (단위: km)
    private static final double RADIUS = 6371;

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // 위도와 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 위도와 경도의 차이 계산
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine 공식을 사용하여 거리 계산
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 km 단위로 반환
        return RADIUS * c;
    }
}
