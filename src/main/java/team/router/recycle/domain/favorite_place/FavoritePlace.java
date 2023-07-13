package team.router.recycle.domain.favorite_place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;
import team.router.recycle.web.favorite_place.FavoritePlaceResponse;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    // 장소 구분
    @Enumerated(EnumType.STRING)
    private Type type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    public enum Type{
        HOME, OFFICE, NORMAL
    }

    @Builder
    public FavoritePlace(final Double latitude, final Double longitude, final Type type, final Member member) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.member = member;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FavoritePlace that = (FavoritePlace) o;
        return Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude)
                && type == that.type && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, type, member);
    }

    public FavoritePlaceResponse toFavoritePlaceResponse() {
        return FavoritePlaceResponse.builder()
                .id(id)
                .name(type.name())
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
