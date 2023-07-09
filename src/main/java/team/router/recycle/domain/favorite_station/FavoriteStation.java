package team.router.recycle.domain.favorite_station;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;

import java.util.Objects;

@Entity
@Getter
@Table(name = "favorite_location")
@Builder
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
    
    @Builder
    public FavoriteStation(Member member, String stationId) {
        this.member = member;
        this.stationId = stationId;
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
}
