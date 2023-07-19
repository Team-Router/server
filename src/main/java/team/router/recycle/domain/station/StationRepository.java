package team.router.recycle.domain.station;

import jakarta.transaction.Transactional;
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
}
