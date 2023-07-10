package team.router.recycle.domain.favorite_station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberRepository;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteStationService {
    
    private final FavoriteStationRepository favoriteStationRepository;
    private final MemberService memberService;
    private final Response response;
    private final MemberRepository memberRepository;
    private final StationRepository stationRepository;
    
    public ResponseEntity<?> addFavoriteStation(String stationId, Long memberId) {
        Station station = stationRepository.findByStationId(stationId).orElse(null);
        if (station == null) {
            return response.fail("해당 역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        
        Member member = memberService.findById(memberId);
        
        FavoriteStation favoriteStation = FavoriteStation.builder()
                .member(member)
                .stationId(stationId)
                .build();
        
        if (favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(stationId, memberId).isPresent()) {
            return response.fail("이미 즐겨찾기에 추가된 역입니다.", HttpStatus.BAD_REQUEST);
        }
        
        member.addFavoriteStation(favoriteStation);
        memberRepository.save(member);
        favoriteStationRepository.save(favoriteStation);
        return response.success();
    }
    
    public ResponseEntity<?> deleteFavoriteStation(String stationId, Long memberId) {
        Member member = memberService.findById(memberId);
        
        Station station = stationRepository.findByStationId(stationId).orElse(null);
        if (station == null) {
            return response.fail("해당 역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        
        FavoriteStation favoriteStation = favoriteStationRepository.findFavoriteStationByStationIdAndMemberId(stationId, memberId).orElse(null);
        
        if (favoriteStation == null) {
            return response.fail("해당 즐겨찾기가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        
        member.deleteFavoriteStation(favoriteStation);
        memberRepository.save(member);
        favoriteStationRepository.delete(favoriteStation);
        return response.success();
    }
    
    public ResponseEntity<?> findAllFavoriteStationByMemberId(Long memberId) {
        List<Station> allByMemberId = favoriteStationRepository.findAllByMemberId(memberId);
        return response.success(allByMemberId);
    }
}
