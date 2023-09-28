package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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
    private List<Category> childs = new ArrayList<>();


    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public Category(String name) {
        this.name = name;
    }
    public void addChild(Category child){
        this.childs.add(child);

    }
}
