package team.router.recycle.domain.favorite_station;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRedisService;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.favorite_station.FavoriteStationResponse;
import team.router.recycle.web.favorite_station.FavoriteStationsResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "favoriteStations")
public class FavoriteStationService {

    private final FavoriteStationRepository favoriteStationRepository;
    private final MemberService memberService;
    private final StationRedisService stationRedisService;

    @Transactional
    @CacheEvict(key = "#memberId")
    public void addFavoriteStation(String stationId, Long memberId) {
        validate(stationId);
        Member member = memberService.getById(memberId);

        FavoriteStation favoriteStation = FavoriteStation.of(stationId, member);

        if (favoriteStationRepository.existsByStationIdAndMemberId(stationId, memberId)) {
            throw new RecycleException(ErrorCode.ALREADY_REGISTERED_FAVORITE, favoriteStation.getStationId() + "는 이미 즐겨찾기에 추가된 대여소입니다.");
        }

        member.addFavoriteStation(favoriteStation);
        favoriteStationRepository.save(favoriteStation);
    }

    private void validate(String stationId) {
        stationRedisService.isValid(stationId);
    }

    @Transactional
    @CacheEvict(key = "#memberId")
    public void deleteFavoriteStation(String stationId, Long memberId) {
        validate(stationId);
        Member member = memberService.getById(memberId);
        FavoriteStation favoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(stationId, memberId)
                .orElseThrow(
                        () -> new RecycleException(ErrorCode.FAVORITE_NOT_FOUND, "즐겨찾기에 등록되지 않은 대여소입니다.")
                );

        member.deleteFavoriteStation(favoriteStation);
        favoriteStationRepository.delete(favoriteStation);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#memberId")
    public FavoriteStationsResponse findAllFavoriteStationByMemberId(Long memberId) {
        List<Station> stations = favoriteStationRepository.findAllByMemberId(memberId);
        return FavoriteStationsResponse.of(
                stations.size(),
                stations.stream()
                        .map(FavoriteStationResponse::from)
                        .collect(Collectors.toList()));
    }
}
