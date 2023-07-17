package team.router.recycle.domain.route.model;

public record Distance(int meters) {
    public static final Distance ZERO = new Distance(0);
}
