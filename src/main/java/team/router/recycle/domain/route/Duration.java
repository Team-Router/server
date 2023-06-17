package team.router.recycle.domain.route;

import lombok.Getter;

@Getter
public class Duration {
    private int seconds;

    public Duration(int seconds) {
        this.seconds = seconds;
    }
}
