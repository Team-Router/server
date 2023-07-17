package team.router.recycle.web.route;

import lombok.Builder;

import java.util.List;

@Builder
public record GetDirectionsResponse(List<GetDirectionResponse> getDirectionsResponses) {

    public static final GetDirectionsResponse EMPTY = GetDirectionsResponse
            .builder()
            .getDirectionsResponses(List.of())
            .build();
}
