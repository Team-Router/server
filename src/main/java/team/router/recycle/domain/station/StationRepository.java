package team.router.recycle.domain.station;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE table station", nativeQuery = true)
    void truncate();

    @Query(value = "SELECT * FROM station ORDER BY ABS(station_latitude - ?1) + ABS(station_longitude - ?2) LIMIT ?3", nativeQuery = true)
    List<Station> findNearestStations(double latitude, double longitude, int count);

    @Query(value = "SELECT * FROM station ORDER BY ABS(station_latitude - ?1) + ABS(station_longitude - ?2) LIMIT 1", nativeQuery = true)
    Station findNearestStations(double latitude, double longitude);

    boolean existsByStationId(String stationId);
}
