package team.router.recycle.domain.route;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Distance {
    private int meters;

    public Distance(int meters) {
        this.meters = meters;
    }
}
