package team.router.recycle.domain.favorite_location;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.web.favorite_location.FavoriteLocationRequest;

@Service
@RequiredArgsConstructor
public class FavoriteLocationService {
    
    private final FavoriteLocationRepository favoriteLocationRepository;
    private final MemberService memberService;
    private final Response response;
    
    public ResponseEntity<?> addFavoriteLocation(FavoriteLocationRequest request, Long memberId) {
        Member member = memberService.findById(memberId);
        FavoriteLocation favoriteLocation = FavoriteLocation.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .member(member)
                .build();
        member.addFavoriteLocation(favoriteLocation);
        favoriteLocationRepository.save(favoriteLocation);
        memberService.save(member);
        
        return response.success(member);
    }
}
