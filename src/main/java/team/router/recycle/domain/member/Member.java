package team.router.recycle.domain.member;

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
import team.router.recycle.domain.favorite_location.FavoriteLocation;
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
    private List<FavoriteLocation> favoriteLocations = new ArrayList<>();
    
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
    
    public void addFavoriteLocation(FavoriteLocation favoriteLocation) {
        this.favoriteLocations.add(favoriteLocation);
    }
}
