package com.booleanuk.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lends")
public class Lend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("lends")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnoreProperties("lends")
    private Item item;

    @Column
    private boolean active;

    public Lend(User user, Item item, boolean active) {
        this.user = user;
        this.item = item;
        this.active = active;
    }

    public Lend(int id) {
        this.id = id;
    }

}
