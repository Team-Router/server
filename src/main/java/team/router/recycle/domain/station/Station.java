package team.router.recycle.domain.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.web.favorite_station.FavoriteStationResponse;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Station {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("stationName")
    private String stationName;

    @JsonProperty("parkingBikeTotCnt")
    @Transient
    private Integer parkingBikeTotCnt;

    @JsonProperty("stationLatitude")
    private Double stationLatitude;

    @JsonProperty("stationLongitude")
    private Double stationLongitude;

    @JsonProperty("stationId")
    private String stationId;

    // to FavoriteStationResponse
    public FavoriteStationResponse toFavoriteStationResponse() {
        return FavoriteStationResponse.builder()
                .name(stationName)
                .latitude(stationLatitude)
                .longitude(stationLongitude)
                .id(stationId)
                .build();
    }

    public String getLocation() {
        return stationLongitude + "," + stationLatitude;
    }
}
