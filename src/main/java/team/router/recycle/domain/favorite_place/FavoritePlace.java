package team.router.recycle.domain.favorite_place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;
import team.router.recycle.web.favorite_place.FavoritePlaceResponse;

import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;    
  
    private String name;
  
    private Double latitude;
    
    private Double longitude;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;
    
    
    @Builder
    public FavoritePlace(final String name, final Double latitude, final Double longitude, final Member member) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.member = member;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoritePlace that = (FavoritePlace) o;
        return Objects.equals(name, that.name) && Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude) && Objects.equals(member, that.member);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude, member);
    }

    public FavoritePlaceResponse toFavoritePlaceResponse() {
        return FavoritePlaceResponse.builder()
                .id(id)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
