package team.router.recycle.domain.favorite_station;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;

import java.util.Objects;

@Entity
@Getter
@Table(name = "favorite_station")
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private FavoriteStation(String stationId, Member member) {
        this(null, stationId, member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteStation that = (FavoriteStation) o;
        return Objects.equals(stationId, that.stationId) && Objects.equals(member, that.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, member);
    }

    public static FavoriteStation of(String stationId, Member member) {
        return new FavoriteStation(stationId, member);
    }
}
