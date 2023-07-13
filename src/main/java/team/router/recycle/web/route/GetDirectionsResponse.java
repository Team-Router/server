package team.router.recycle.web.route;

import lombok.Builder;

import java.util.List;

@Builder
public record GetDirectionsResponse(List<GetDirectionResponse> getDirectionsResponses) {
}
