package team.router.recycle.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.RouteService;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.domain.route.model.RoutingProfile;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.route.GetDirectionRequest;
import team.router.recycle.web.route.GetDirectionResponse;
import team.router.recycle.web.route.GetDirectionsResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class RouteServiceTest {
    
    @Autowired
    private RouteService routeService;

    @Nested
    @DisplayName("걷기 경로 탐색")
    class WalkDirection {

        @Test
        @DisplayName("성공")
        void getWalkDirectionSuccess() {
            GetDirectionRequest getDirectionRequest = new GetDirectionRequest(new Location(37.4864406, 127.1030211), new Location(37.553001, 126.924558));
            GetDirectionResponse response = routeService.getWalkDirection(getDirectionRequest);

            assertAll(() -> {
                assertThat(response).isNotNull();
                assertThat(response.routingProfile()).isEqualTo(RoutingProfile.pedestrian);
                assertThat(response.getDuration()).isGreaterThan(0);
                assertThat(response.getDuration()).isGreaterThan(0);
                assertThat(response.locations()).isNotNull();
            });
        }

        @Test
        @DisplayName("실패 - 경로 탐색 실패(30km 초과)")
        void getWalkDirectionFail() {
            GetDirectionRequest getDirectionRequest = new GetDirectionRequest(new Location(37.4864406, 127.1030211), new Location(45.553001, 140.924558));

            assertThatExceptionOfType(RecycleException.class)
                    .isThrownBy(() -> routeService.getWalkDirection(getDirectionRequest))
                    .withMessage("직선거리 30km를 초과했습니다.");
        }
    }
    
    @Nested
    @DisplayName("걷기, 자전거, 걷기")
    class CycleDirection {
        
        @Test
        @DisplayName("성공")
        void getCycleDirectionSuccess() {
            GetDirectionRequest getDirectionRequest = new GetDirectionRequest(new Location(37.4864406, 127.1030211), new Location(37.553001, 126.924558));
            GetDirectionsResponse response = routeService.getCycleDirection(getDirectionRequest);
            
            assertAll(() -> {
                assertThat(response).isNotNull();
                assertThat(response.getDirectionsResponses().size()).isEqualTo(3);
                
                assertDirectionResponse(response.getDirectionsResponses().get(0), RoutingProfile.pedestrian);
                assertDirectionResponse(response.getDirectionsResponses().get(1), RoutingProfile.cyclability);
                assertDirectionResponse(response.getDirectionsResponses().get(2), RoutingProfile.pedestrian);
            });
        }

        @Test
        @DisplayName("실패 - 경로 탐색 실패(500m 미만)")
        void getCycleDirectionFail() {
            GetDirectionRequest getDirectionRequest = new GetDirectionRequest(new Location(37.4864406, 127.1030211), new Location(37.4864406, 127.1030211));

            assertThatExceptionOfType(RecycleException.class)
                    .isThrownBy(() -> routeService.getCycleDirection(getDirectionRequest))
                    .withMessage("가까운 거리는 도보 경로를 이용해주세요.");
        }

        private void assertDirectionResponse(GetDirectionResponse directionResponse, RoutingProfile expectedRoutingProfile) {
            assertThat(directionResponse.routingProfile()).isEqualTo(expectedRoutingProfile);
            assertThat(directionResponse.getDuration()).isGreaterThanOrEqualTo(0);
            assertThat(directionResponse.locations()).isNotNull();
        }
    }
}
