package team.router.recycle.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.station.StationService;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationsRealtimeResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Nested
    @DisplayName("실시간 대여소 조회")
    class GetRealtimeStation {
        @ParameterizedTest
        @DisplayName("대여소 조회 성공")
        @CsvSource({"37.5666785, 126.9784679", "37.541, 126.986"})
        void getRealtimeStationSuccess(double latitude, double longitude) {
            StationRealtimeRequest stationRealtimeRequest = new StationRealtimeRequest(latitude, longitude);

            StationsRealtimeResponse response = stationService.getRealtimeStation(stationRealtimeRequest);

            assertAll(() -> {
                        assertThat(response).isNotNull();
                        assertThat(response.count()).isGreaterThanOrEqualTo(0);
                        assertThat(response.stationRealtimeResponses()).isNotNull();
                    }
            );
        }
    }
}