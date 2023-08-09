package team.router.recycle.domain.station;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonAlias({"stationName", "name"})
    private String stationName;

    @JsonAlias({"parkingBikeTotCnt", "parking_count"})
    @Transient
    private Integer parkingBikeTotCnt;

    @JsonAlias({"stationLatitude", "x_pos"})
    private Double stationLatitude;

    @JsonAlias({"stationLongitude", "y_pos"})
    private Double stationLongitude;

    @JsonAlias({"stationId", "id"})
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

    public String toLocationString() {
        return stationLongitude + "," + stationLatitude;
    }
}
