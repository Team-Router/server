package team.router.recycle.domain.favorite_station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.Response;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;

import java.util.List;

@Service
@RequiredArgsConstructor

public class FavoriteStationService {

    private final FavoriteStationRepository favoriteStationRepository;
    private final MemberService memberService;
    private final Response response;
    private final StationRepository stationRepository;

    @Transactional
    public ResponseEntity<?> addFavoriteStation(String stationId, Long memberId) {
        validate(stationId);
        Member member = memberService.getById(memberId);

        FavoriteStation favoriteStation = FavoriteStation.builder()
                .member(member)
                .stationId(stationId)
                .build();

        favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(stationId, memberId)
                .ifPresent(f -> {
                            throw new RecycleException(ErrorCode.ALREADY_REGISTERED_FAVORITE, f.getStationId() + "는 이미 즐겨찾기에 추가된 대여소입니다.");
                        }
                );

        member.addFavoriteStation(favoriteStation);
        favoriteStationRepository.save(favoriteStation);
        return response.success();
    }

    private void validate(String stationId) {
        if (!stationRepository.existsByStationId(stationId)) {
            throw new RecycleException(ErrorCode.STATION_NOT_FOUND, "해당 대여소가 존재하지 않습니다.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteFavoriteStation(String stationId, Long memberId) {
        validate(stationId);
        Member member = memberService.getById(memberId);
        FavoriteStation favoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(stationId, memberId)
                .orElseThrow(
                        () -> new RecycleException(ErrorCode.FAVORITE_NOT_FOUND, "즐겨찾기에 등록되지 않은 대여소입니다.")
                );

        member.deleteFavoriteStation(favoriteStation);
        favoriteStationRepository.delete(favoriteStation);
        return response.success();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllFavoriteStationByMemberId(Long memberId) {
        List<Station> stationsByMemberId = favoriteStationRepository.findAllByMemberId(memberId);
        return response.success(stationsByMemberId);
    }
}
