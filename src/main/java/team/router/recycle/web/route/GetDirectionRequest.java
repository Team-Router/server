package team.router.recycle.web.route;

import team.router.recycle.domain.route.model.Location;

public record GetDirectionRequest(Location startLocation, Location endLocation) {
}