package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;


//    orphanremoval, cascade는 문제시 수정해야함
    @OneToMany(mappedBy = "parent",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<Category> childs;


}
