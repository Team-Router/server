package team.router.recycle.web.favorite_place;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.favorite_place.FavoritePlaceService;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class FavoritePlaceController {
    
    private final FavoritePlaceService favoritePlaceService;
    
    
    @PostMapping()
    public ResponseEntity<?> addFavoritePlace(@RequestBody FavoritePlaceRequest request) {
        favoritePlaceService.addFavoritePlace(SecurityUtil.getCurrentMemberId(), request);
        return ResponseEntity.ok().build();
    }
    
    
    @DeleteMapping()
    public ResponseEntity<?> deleteFavoritePlace(@RequestParam Long favoriteId) {
        favoritePlaceService.deleteFavoritePlace(SecurityUtil.getCurrentMemberId(), favoriteId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping()
    public ResponseEntity<?> findAllFavoritePlace() {
        FavoritePlacesResponse allFavoritePlace = favoritePlaceService.findAllFavoritePlace(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok(allFavoritePlace);
    }
    
    // Pageable: getPageNumber, getPageSize, getOffset, ...
    // Todo: findAllFavoritePlace랑 나중에 바꾸기
    @GetMapping("/paging_test")
    public ResponseEntity<?> findAllFavoritePlacePaging(Pageable pageable) {
        FavoritePlacesResponse allFavoritePlace = favoritePlaceService.findAllFavoritePlacePaging(SecurityUtil.getCurrentMemberId(), pageable);
        return ResponseEntity.ok(allFavoritePlace);
    }
}
