package com.veggie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_knowledge")
public class Knowledge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vegetable_id")
    private Integer vegetableId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "content_text", nullable = false, columnDefinition = "TEXT")
    private String contentText;

    public Integer getId() { return id; }
    public Integer getVegetableId() { return vegetableId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContentText() { return contentText; }
}
