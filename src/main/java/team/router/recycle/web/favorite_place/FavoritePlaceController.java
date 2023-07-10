package team.router.recycle.web.favorite_place;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.favorite_place.FavoritePlaceService;
import team.router.recycle.domain.favorite_place.FavoritePlacesResponse;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;


    // request
    @PostMapping(path = "/add")
    public ResponseEntity<?> addFavoritePlace(@RequestBody FavoritePlaceRequest.AddFavoritePlace request) {
        favoritePlaceService.addFavoritePlace(SecurityUtil.getCurrentMemberId(), request);
        return ResponseEntity.ok().build();
    }


    // 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFavoritePlace(@RequestParam Long favoriteId) {
        favoritePlaceService.deleteFavoritePlace(SecurityUtil.getCurrentMemberId(), favoriteId);
        return ResponseEntity.ok().build();
    }

    // 전체 조회
    @GetMapping("/find")
    public ResponseEntity<?> findAllFavoritePlace() {
        FavoritePlacesResponse allFavoritePlace = favoritePlaceService.findAllFavoritePlace(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok(allFavoritePlace);
    }
}
