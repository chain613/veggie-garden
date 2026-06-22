package com.veggie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_item")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(length = 500)
    private String description;

    public enum ItemType { seed, tool, fertilizer, pesticide, organic }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
