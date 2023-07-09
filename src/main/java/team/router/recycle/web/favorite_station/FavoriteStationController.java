package team.router.recycle.web.favorite_station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.favorite_station.FavoriteStationService;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteStationController {
    
    private final FavoriteStationService favoriteStationService;
    
    // 추가
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteStation(@RequestParam String stationId) {
        return favoriteStationService.addFavoriteStation(stationId, SecurityUtil.getCurrentMemberId());
    }
    
    // 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFavoriteStation(@RequestParam String stationId) {
        return favoriteStationService.deleteFavoriteStation(stationId, SecurityUtil.getCurrentMemberId());
    }
    
    // 조회
    @GetMapping("/find")
    public ResponseEntity<?> findAllFavoriteStationByMemberId() {
        return favoriteStationService.findAllFavoriteStationByMemberId(SecurityUtil.getCurrentMemberId());
    }
}
