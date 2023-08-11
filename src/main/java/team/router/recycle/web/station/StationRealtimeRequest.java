package team.router.recycle.web.station;

public record StationRealtimeRequest(
        Double latitude,
        Double longitude
) {

    public String getCity() {
        if (latitude > 37) {
            return "seoul";
        } else {
            return "daejeon";
        }
    }
}
