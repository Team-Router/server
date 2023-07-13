package team.router.recycle.domain.route.model;

public record Location(double latitude, double longitude) {
    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}