package com.pokeworld.pokedex.models;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;

public class itemsDto {

    private int id;

    @NotEmpty(message = "Name is required")
    private String name_item;

    @NotEmpty(message = "Effect is required")
    private String effect;

    private MultipartFile picture;

    private String oldPicture; // nama file gambar lama

    @NotEmpty(message = "Category is required")
    private String category;

    private Integer flingPower;

    private String attributes;

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

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public String getOldPicture() {
        return oldPicture;
    }

    public void setOldPicture(String oldPicture) {
        this.oldPicture = oldPicture;
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
}
