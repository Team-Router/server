package team.router.recycle.domain.favorite_place;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberRepository;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.web.favorite_place.FavoritePlaceRequest;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoritePlaceRepository favoritePlaceRepository;

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final Response response;

    public ResponseEntity<?> addFavoritePlace(Long memberId, FavoritePlaceRequest.AddFavoritePlace request) {
        Member member = memberService.findById(memberId);

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .type(request.getType())
                .member(member)
                .build();

        if (member.addFavoritePlace(favoritePlace)){
            memberRepository.save(member);
            favoritePlaceRepository.save(favoritePlace);
            return response.success();
        }
        return response.fail("이미 저장된 장소 입니다.", HttpStatus.BAD_REQUEST);
    }

    // 전체 조회

    public ResponseEntity<?> findAllFavoritePlace(Long memberId){
        List<FavoritePlace> favoritePlaceList = favoritePlaceRepository.findAllByMemberId(memberId);
        return response.success(favoritePlaceList);
    }

    // 삭제
    public ResponseEntity<?> deleteFavoritePlace(Long memberId, Long favoritePlaceId){
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(favoritePlaceId).orElse(null);

        if (favoritePlace == null){
            return response.fail("존재하지 않는 장소입니다.", HttpStatus.BAD_REQUEST);
        }

        if (!memberId.equals(favoritePlace.getMember().getId())){
            return response.fail("회원 번호가 일치되지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        favoritePlaceRepository.deleteById(favoritePlaceId);
        return response.success();
    }
}
