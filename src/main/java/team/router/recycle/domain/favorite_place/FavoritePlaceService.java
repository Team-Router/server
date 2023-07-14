package team.router.recycle.domain.favorite_place;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.favorite_place.FavoritePlaceRequest;
import team.router.recycle.web.favorite_place.FavoritePlacesResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {
    
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final MemberService memberService;
    
    public void addFavoritePlace(Long memberId, FavoritePlaceRequest request) {
        Member member = memberService.getById(memberId);
        String name = request.name();
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        
        if (favoritePlaceRepository.existsFavoritePlaceByNameAndLatitudeAndLongitudeAndMemberId(name, latitude, longitude, memberId)) {
            throw new RecycleException(ErrorCode.ALREADY_REGISTERED_FAVORITE, "이미 저장된 장소 입니다.");
        }
        
        FavoritePlace favoritePlace = FavoritePlace.builder()
                .name(name)
                .longitude(longitude)
                .latitude(latitude)
                .member(member)
                .build();
        
        if (name.equals("HOME") || name.equals("OFFICE")) {
            FavoritePlace existingPlace = favoritePlaceRepository.findByName(name).orElse(null);
            if (existingPlace != null) {
                member.deleteFavoritePlace(existingPlace);
                favoritePlaceRepository.delete(existingPlace);
            }
        }
        
        member.addFavoritePlace(favoritePlace);
        favoritePlaceRepository.save(favoritePlace);
    }
    
    public void deleteFavoritePlace(Long memberId, Long favoritePlaceId) {
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(favoritePlaceId).orElseThrow(
                () -> new RecycleException(ErrorCode.FAVORITE_NOT_FOUND, "즐겨찾기에 등록되지 않은 장소입니다.")
        );
        Member member = memberService.getById(memberId);
        member.deleteFavoritePlace(favoritePlace);
        favoritePlaceRepository.delete(favoritePlace);
    }
    
    public FavoritePlacesResponse findAllFavoritePlace(Long memberId) {
        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByMemberId(memberId);
        return FavoritePlacesResponse.builder()
                .count(favoritePlaces.size())
                .favoritePlaces(favoritePlaces
                        .stream()
                        .map(FavoritePlace::toFavoritePlaceResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    public FavoritePlacesResponse findAllFavoritePlacePaging(Long memberId, Pageable pageable) {
        Page<FavoritePlace> byMemberOrderByIdDesc = favoritePlaceRepository.findByMemberId(memberId, pageable);
        return FavoritePlacesResponse.builder()
                .count(byMemberOrderByIdDesc.getContent().size())
                .favoritePlaces(byMemberOrderByIdDesc
                        .stream()
                        .map(FavoritePlace::toFavoritePlaceResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
