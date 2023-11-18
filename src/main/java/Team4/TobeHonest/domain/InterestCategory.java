package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class InterestCategory {
    @Id
    @GeneratedValue()
    @Column(name = "interest_category_id")
    private Long id;


    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

//    그외 속성들 추가할거 있으면 추가하기

}
