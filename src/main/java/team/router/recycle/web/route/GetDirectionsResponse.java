package team.router.recycle.web.route;

import java.util.List;

public record GetDirectionsResponse(List<GetDirectionResponse> getDirectionsResponses) {

    public static GetDirectionsResponse from(List<GetDirectionResponse> getDirectionsResponses) {
        return new GetDirectionsResponse(getDirectionsResponses);
    }

    public static final GetDirectionsResponse EMPTY = GetDirectionsResponse.from(List.of());
}
