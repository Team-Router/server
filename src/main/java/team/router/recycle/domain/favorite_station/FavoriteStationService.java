//package team.router.recycle.domain.favorite_station;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import team.router.recycle.Response;
//import team.router.recycle.domain.member.Member;
//import team.router.recycle.domain.member.MemberService;
//import team.router.recycle.web.favorite_station.FavoriteStationRequest;
//
//@Service
//@RequiredArgsConstructor
//public class FavoriteStationService {
//
//    private final FavoriteStationRepository favoriteStationRepository;
//    private final MemberService memberService;
//    private final Response response;
//
//    public ResponseEntity<?> addFavoriteLocation(FavoriteStationRequest request, Long memberId) {
//        Member member = memberService.findById(memberId);
//        FavoriteStation favoriteStation = FavoriteStation.builder()
//                .name(request.getName())
//                .latitude(request.getLatitude())
//                .longitude(request.getLongitude())
//                .member(member)
//                .build();
//        member.addFavoriteLocation(favoriteStation);
//        favoriteStationRepository.save(favoriteStation);
//        memberService.save(member);
//
//        return response.success(member);
//    }
//}
