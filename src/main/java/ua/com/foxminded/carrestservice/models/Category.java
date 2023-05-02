package ua.com.foxminded.carrestservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "category_model",
            joinColumns = @JoinColumn(name = "category_ref"),
            inverseJoinColumns = @JoinColumn(name = "model_ref"))
    private Set<Model> models = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }

    public void addModel(Model model) {
        models.add(model);
    }

    public void removeModel(Model model){
        models.remove(model);
    }
}