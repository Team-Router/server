package team.router.recycle.domain.favorite_place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberRepository;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.favorite_place.FavoritePlaceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoritePlaceRepository favoritePlaceRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public void addFavoritePlace(Long memberId, FavoritePlaceRequest.AddFavoritePlace request) {
        Member member = memberService.getById(memberId);
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();
        FavoritePlace.Type type = request.getType();

        if (favoritePlaceRepository.existsFavoritePlaceByLatitudeAndLongitudeAndMemberId(latitude, longitude, memberId)) {
            throw new RecycleException(ErrorCode.ALREADY_REGISTERED_FAVORITE, "이미 저장된 장소 입니다.");
        }

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .longitude(longitude)
                .latitude(latitude)
                .type(type)
                .member(member)
                .build();

        member.addFavoritePlace(favoritePlace);
        favoritePlaceRepository.save(favoritePlace);
    }

    public void deleteFavoritePlace(Long memberId, Long favoritePlaceId) {
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(favoritePlaceId).orElseThrow(
                () -> new RecycleException(ErrorCode.FAVORITE_NOT_FOUND, "즐겨찾기에 등록되지 않은 장소입니다.")
        );

//        if (!memberId.equals(favoritePlace.getMember().getId())) {
//            return response.fail("회원 번호가 일치되지 않습니다.", HttpStatus.BAD_REQUEST);
//        }

        Member member = memberService.getById(memberId);
        member.deleteFavoritePlace(favoritePlace);
        favoritePlaceRepository.delete(favoritePlace);
    }

    public FavoritePlacesResponse findAllFavoritePlace(Long memberId) {
        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByMemberId(memberId);
        return new FavoritePlacesResponse(favoritePlaces.size(),
                favoritePlaces
                        .stream()
                        .map(fp ->
                                new FavoritePlaceResponse(fp.getFavoritePlaceId(), fp.getType().name(), fp.getLatitude(), fp.getLongitude()))
                        .collect(Collectors.toList()));
    }
}
