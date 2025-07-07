package com.pokeworld.pokedex.models;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name_item", nullable = false)
    private String name_item;

    @Column(columnDefinition = "TEXT")
    private String effect;

    private String picture;

    private String category;

    @Column(nullable = true)
    private Integer flingPower;

    @Column(columnDefinition = "TEXT")
    private String attributes; // ex: "consumable, usable-in-battle"

    public item() {
    }

    public item(String name_item, String effect, String picture, String category, Integer flingPower, String attributes) {
        this.name_item = name_item;
        this.effect = effect;
        this.picture = picture;
        this.category = category;
        this.flingPower = flingPower;
        this.attributes = attributes;
    }

    // Getter & Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_item() {
        return name_item;
    }

    public void setName_item(String name_item) {
        this.name_item = name_item;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getFlingPower() {
        return flingPower;
    }

    public void setFlingPower(Integer flingPower) {
        this.flingPower = flingPower;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name_item='" + name_item + '\'' +
                ", effect='" + effect + '\'' +
                ", picture='" + picture + '\'' +
                ", category='" + category + '\'' +
                ", flingPower=" + flingPower +
                ", attributes='" + attributes + '\'' +
                '}';
    }
}
