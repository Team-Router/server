package team.router.recycle.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.favorite_place.FavoritePlace;
import team.router.recycle.domain.favorite_station.FavoriteStation;
import team.router.recycle.util.BooleanYNConverter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Enumerated(EnumType.STRING)
    private final Authority authority = Authority.ROLE_USER;
    
    @OneToMany(mappedBy = "member")
    private List<FavoriteStation> favoriteStations = new ArrayList<>();
    
    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<FavoritePlace> favoritePlaces = new ArrayList<>();
    
    public void addFavoriteStation(FavoriteStation favoriteStation) {
        favoriteStations.add(favoriteStation);

    }
    
    public void deleteFavoriteStation(FavoriteStation favoriteStation) {
        favoriteStations.remove(favoriteStation);
    }
    
    public void addFavoritePlace(FavoritePlace favoritePlace) {
        favoritePlaces.add(favoritePlace);
    }
    
    public void deleteFavoritePlace(FavoritePlace favoritePlace) {
        favoritePlaces.remove(favoritePlace);
    }
    
    
    @Convert(converter = BooleanYNConverter.class)
    private Boolean isDeleted = Boolean.FALSE;
    
    @Builder
    public Member(String email, Type type, Boolean isDeleted) {
        this.email = email;
        this.type = type;
        this.isDeleted = isDeleted;
    }
    
    public void delete() {
        this.isDeleted = Boolean.TRUE;
    }
    
    public enum Type {
        GOOGLE, NAVER, KAKAO
    }
    
    public enum Authority {
        ROLE_USER, ROLE_ADMIN
    }
}
