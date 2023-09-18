package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class InterestCategory {
    @Id @GeneratedValue
    @Column(name = "interest_category_id")
    private Long id;


    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

//    그외 속성들 추가할거 있으면 추가하기

}
