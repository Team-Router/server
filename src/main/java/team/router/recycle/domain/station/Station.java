package team.router.recycle.domain.station;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationNumber;
    private String stationName;
    private String stationNumberName;
    private String stationAddress1;
    private String stationAddress2;
    private Double stationLatitude;
    private Double stationLongitude;
}
