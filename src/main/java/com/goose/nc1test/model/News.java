package com.goose.nc1test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "headline")
    private String headline;

    @Column(name = "description", length = 100000)
    private String description;

    @Column(name = "publication_time")
    private LocalTime time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        News o1 = (News) o;
        return id != null && id.equals(o1.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
