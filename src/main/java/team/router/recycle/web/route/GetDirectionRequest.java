package team.router.recycle.web.route;

import team.router.recycle.domain.route.model.Location;

public record GetDirectionRequest(Location startLocation, Location endLocation) {

    public boolean isInvalidCycleRequest(GetDirectionRequest request) {
        return haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) < 0.5;
    }

    public boolean isInvalidWalkRequest(GetDirectionRequest request) {
        return haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) > 30;
    }

    // 두 location 사이의 거리를 구하는 함수
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // 지구의 반지름 (단위: km)
        double radius = 6371;

        // 위도와 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 위도와 경도의 차이 계산
        double dlat = lat2Rad - lat1Rad;
        double dlon = lon2Rad - lon1Rad;

        // Haversine 공식을 사용하여 거리 계산
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 km 단위로 반환
        return radius * c;
    }
}