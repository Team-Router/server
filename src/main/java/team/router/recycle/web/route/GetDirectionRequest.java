package team.router.recycle.web.route;

import team.router.recycle.domain.route.model.Location;
import team.router.recycle.util.GeoUtil;

public record GetDirectionRequest(Location startLocation, Location endLocation) {

    public boolean isInvalidCycleRequest(GetDirectionRequest request) {
        return GeoUtil.haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) < 0.5;
    }

    public boolean isInvalidWalkRequest(GetDirectionRequest request) {
        return GeoUtil.haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) > 30;
    }
}