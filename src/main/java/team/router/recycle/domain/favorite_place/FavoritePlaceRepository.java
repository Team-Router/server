package team.router.recycle.domain.favorite_place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
    
    boolean existsFavoritePlaceByLatitudeAndLongitudeAndMemberId(Double latitude, Double longitude, Long memberId);
    
    List<FavoritePlace> findAllByMemberId(Long memberId);
}
