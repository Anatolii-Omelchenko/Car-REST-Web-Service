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
@Table(name = "models", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "production_year", "brand_ref"})
})
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "brand_ref", nullable = false)
    private Brand brand;

    @Column(name = "production_year", nullable = false)
    private Integer productionYear;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "models", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Car> cars = new HashSet<>();

    public Model(String name, Brand brand, Integer productionYear) {
        this.name = name;
        this.brand = brand;
        this.productionYear = productionYear;
    }
}