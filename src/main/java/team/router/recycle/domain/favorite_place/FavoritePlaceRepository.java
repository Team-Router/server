package team.router.recycle.domain.favorite_place;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.router.recycle.domain.station.Station;

@Repository
public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {

    @Query(value = "select * from favorite_place where member_id = ?1", nativeQuery = true)
    List<FavoritePlace> findAllByMemberId(Long memberId);
}
