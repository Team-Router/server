package team.router.recycle.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.favorite_station.FavoriteStation;
import team.router.recycle.domain.favorite_station.FavoriteStationRepository;
import team.router.recycle.domain.favorite_station.FavoriteStationService;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberRepository;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;
import team.router.recycle.web.exception.RecycleException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class FavoriteStationServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FavoriteStationService favoriteStationService;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private FavoriteStationRepository favoriteStationRepository;

    @Nested
    @DisplayName("즐겨찾기 등록")
    class AddFavoriteStationTest {

        @Test
        @DisplayName("성공")
        void addFavoriteStationSuccess() {
            Member member = createMember();
            Station station = createStation("test");
            Long memberId = member.getId();

            favoriteStationService.addFavoriteStation(station.getStationId(), memberId);

            Optional<FavoriteStation> maybeFavoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(station.getStationId(), memberId);

            assertAll(() -> {
                assertThat(maybeFavoriteStation.isPresent()).isTrue();
                FavoriteStation favoriteStation = maybeFavoriteStation.get();

                assertThat(favoriteStation.getStationId()).isEqualTo("test");
                assertThat(favoriteStation.getMember()).isEqualTo(member);
            });
        }

        @Test
        @DisplayName("중복 즐겨찾기 등록")
        void addFavoriteStationFail() {
            Member member = createMember();
            Station station = createStation("test");
            Long memberId = member.getId();
            favoriteStationService.addFavoriteStation(station.getStationId(), memberId);

            FavoriteStation favoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(station.getStationId(), memberId).get();

            assertThatThrownBy(() -> favoriteStationService.addFavoriteStation(station.getStationId(), memberId))
                    .isInstanceOf(RecycleException.class)
                    .hasMessage(favoriteStation.getStationId() + "는 이미 즐겨찾기에 추가된 대여소입니다.");
        }

        @Test
        @DisplayName("존재하지 않는 대여소 즐겨찾기 등록")
        void addFavoriteStationFail2() {
            Member member = createMember();
            String wrongStationId = "wrongStationId";

            assertThatThrownBy(() -> favoriteStationService.addFavoriteStation(wrongStationId, member.getId()))
                    .isInstanceOf(RecycleException.class)
                    .hasMessage("해당 대여소가 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("즐겨찾기 삭제")
    class DeleteFavoriteStation {

        @Test
        @DisplayName("성공")
        void deleteFavoriteStationSuccess() {
            Member member = createMember();
            Station station = createStation("test");
            Long memberId = member.getId();
            favoriteStationService.addFavoriteStation(station.getStationId(), memberId);

            favoriteStationService.deleteFavoriteStation(station.getStationId(), memberId);

            Optional<FavoriteStation> maybeFavoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(station.getStationId(), memberId);
            assertAll(() -> {
                assertThat(maybeFavoriteStation.isPresent()).isFalse();
                assertThat(member.getFavoriteStations().size()).isEqualTo(0);
            });
        }

        @Test
        @DisplayName("존재하지 않는 즐겨찾기 삭제")
        void deleteFavoriteStationFail() {
            Member member = createMember();
            Station station = createStation("test");
            Long memberId = member.getId();

            assertThatThrownBy(() -> favoriteStationService.deleteFavoriteStation(station.getStationId(), memberId))
                    .isInstanceOf(RecycleException.class)
                    .hasMessage("즐겨찾기에 등록되지 않은 대여소입니다.");
        }
    }

    @Nested
    @DisplayName("즐겨찾기 조회")
    class FindAllFavoriteStationByMemberId {
        @Test
        @DisplayName("여러건 조회")
        void findAllFavoriteStationByMemberIdSuccess() {
            Member member = createMember();
            Station station = createStation("test");
            Station station2 = createStation("test2");
            Long memberId = member.getId();
            favoriteStationService.addFavoriteStation(station.getStationId(), memberId);
            favoriteStationService.addFavoriteStation(station2.getStationId(), memberId);

            assertAll(() -> {
                assertThat(favoriteStationService.findAllFavoriteStationByMemberId(memberId).count()).isEqualTo(2);
                assertThat(favoriteStationService.findAllFavoriteStationByMemberId(memberId).favoriteStationResponses().size()).isEqualTo(2);
            });
        }

        @Test
        @DisplayName("빈 리스트 조회")
        void findAllFavoriteStationByMemberEmpty() {
            Member member = createMember();
            Long memberId = member.getId();

            assertThat(favoriteStationService.findAllFavoriteStationByMemberId(memberId).count()).isEqualTo(0);
        }
    }


    private Member createMember() {
        Member member = new Member();
        return memberRepository.saveAndFlush(member);
    }

    private Station createStation(String stationId) {
        Station station = Station.builder()
                .stationId(stationId).build();
        return stationRepository.saveAndFlush(station);
    }
}