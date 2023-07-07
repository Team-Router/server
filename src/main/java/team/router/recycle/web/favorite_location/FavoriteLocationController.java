package team.router.recycle.web.favorite_location;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.favorite_location.FavoriteLocationService;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteLocationController {
    
    private final FavoriteLocationService favoriteLocationService;
    
    // 추가
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteLocation(@RequestBody FavoriteLocationRequest request) {
        System.out.println(request.getName());
        Long memberId = SecurityUtil.getCurrentMemberId();
        System.out.println(memberId);
        return favoriteLocationService.addFavoriteLocation(request, memberId);
    }
}
