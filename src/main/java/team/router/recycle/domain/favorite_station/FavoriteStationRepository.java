package team.router.recycle.domain.favorite_station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import team.router.recycle.domain.station.Station;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteStationRepository extends JpaRepository<FavoriteStation, Long> {
    
    Optional<FavoriteStation> findFavoriteStationByStationIdAndMemberId(String stationId, Long memberId);

    @Query("SELECT s from Station s where s.stationId in (select f.stationId from FavoriteStation f where f.member.id = :memberId)")
    List<Station> findAllByMemberId(Long memberId);

    boolean existsByStationIdAndMemberId(String stationId, Long memberId);
}
