package team.router.recycle.web.favorite_place;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
}
