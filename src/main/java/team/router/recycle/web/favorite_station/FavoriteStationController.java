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
    
    @PostMapping()
    public ResponseEntity<?> addFavoriteStation(@RequestParam String stationId) {
        favoriteStationService.addFavoriteStation(stationId, SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping()
    public ResponseEntity<?> deleteFavoriteStation(@RequestParam String stationId) {
        favoriteStationService.deleteFavoriteStation(stationId, SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping()
    public ResponseEntity<?> findAllFavoriteStationByMemberId() {
        return ResponseEntity.ok(favoriteStationService.findAllFavoriteStationByMemberId(SecurityUtil.getCurrentMemberId()));
    }
}
