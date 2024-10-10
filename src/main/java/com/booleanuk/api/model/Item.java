package com.booleanuk.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private boolean occupied;

    @OneToMany(mappedBy = "item")
    @JsonIgnoreProperties("item")
    private List<Lend> lends;

    public Item(int id) {
        this.id = id;
    }

    public void addLend(Lend l){
        this.lends.add(l);
    }

}
