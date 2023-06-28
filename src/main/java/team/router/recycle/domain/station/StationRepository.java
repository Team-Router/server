package team.router.recycle.domain.station;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE table station", nativeQuery = true)
    void truncate();

    @Query(value = "SELECT * FROM station ORDER BY ABS(station_latitude - ?1) + ABS(station_longitude - ?2) LIMIT ?3", nativeQuery = true)
    List<Station> findNearestStation(double latitude, double longitude, int count);
}