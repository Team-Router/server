package team.router.recycle.web.station;

public record StationRealtimeResponse(
        String stationName,
        Integer parkingBikeTotCnt,
        Double stationLatitude,
        Double stationLongitude,
        String stationId
) {
}
