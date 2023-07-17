package team.router.recycle.domain.route.model;

public record Duration(int seconds) {
    public static final Duration ZERO = new Duration(0);
}