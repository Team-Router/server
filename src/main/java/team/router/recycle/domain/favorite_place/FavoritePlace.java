package team.router.recycle.domain.favorite_place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;
import team.router.recycle.web.favorite_place.FavoritePlaceRequest;

import java.util.Objects;

@Entity
@Getter
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

    public static FavoritePlace of(FavoritePlaceRequest request, Member member) {

        return new FavoritePlace(null, request.name(), request.latitude(), request.longitude(), member);
    }
}
