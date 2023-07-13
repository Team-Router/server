package team.router.recycle.domain.favorite_place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
    
    boolean existsFavoritePlaceByLatitudeAndLongitudeAndMemberIdAndType(Double latitude, Double longitude, Long memberId, FavoritePlace.Type type);
    
    List<FavoritePlace> findAllByMemberId(Long memberId);
    
    Optional<FavoritePlace> findByType(FavoritePlace.Type type);
}
