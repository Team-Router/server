package team.router.recycle.domain.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Query(value = "SELECT * FROM station ORDER BY ABS(station_latitude - ?1) + ABS(station_longitude - ?2) LIMIT 1", nativeQuery = true)
    Station findNearestStation(double latitude, double longitude);
}
