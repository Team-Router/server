package team.router.recycle.web.favorite_station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteStationController {
    
//    private final FavoriteStationService favoriteStationService;
    
    // 추가
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteLocation(@RequestParam("stationId") String stationId) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        System.out.println("stationId = " + stationId);
        System.out.println("memberId = " + memberId);
        return ResponseEntity.ok().build();
    }
}
