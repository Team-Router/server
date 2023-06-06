package team.router.recycle;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationId;
    private String stationAddress1;
    private String stationAddress2;
    private Double stationLatitude;
    private Double stationLongitude;

    public Station(String stationId, String stationAddress1, String stationAddress2, Double stationLatitude, Double stationLongitude) {
        this.stationId = stationId;
        this.stationAddress1 = stationAddress1;
        this.stationAddress2 = stationAddress2;
        this.stationLatitude = stationLatitude;
        this.stationLongitude = stationLongitude;
    }

    public Station() {

    }
}
