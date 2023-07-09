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

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteStationService {
    
    private final FavoriteStationRepository favoriteStationRepository;
    private final MemberService memberService;
    private final Response response;
    private final MemberRepository memberRepository;
    
    public ResponseEntity<?> addFavoriteStation(String stationId, Long memberId) {
        Member member = memberService.findById(memberId);
        FavoriteStation favoriteStation = FavoriteStation.builder()
                .member(member)
                .stationId(stationId)
                .build();
        
        if (member.addFavoriteStation(favoriteStation)) {
            memberRepository.save(member);
            favoriteStationRepository.save(favoriteStation);
            return response.success();
        }
        return response.fail("이미 즐겨찾기에 추가된 역입니다.", HttpStatus.BAD_REQUEST);
    }
    
    public ResponseEntity<?> deleteFavoriteStation(String stationId, Long memberId) {
        Member member = memberService.findById(memberId);
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
