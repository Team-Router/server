package team.router.recycle.domain.station;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("RENT_NO")
    private String stationNumber;

    @JsonProperty("RENT_NM")
    private String stationName;

    @JsonProperty("RENT_ID_NM")
    private String stationNumberName;

    @JsonProperty("STA_ADD1")
    private String stationAddress1;

    @JsonProperty("STA_ADD2")
    private String stationAddress2;

    @JsonProperty("STA_LAT")
    private Double stationLatitude;

    @JsonProperty("STA_LONG")
    private Double stationLongitude;
}
