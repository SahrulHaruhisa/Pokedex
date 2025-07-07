package com.pokeworld.pokedex.models;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type1;
    private String type2;

    @Column(name = "image_url")
    private String imageUrl;

    private String evolution1;
    private String evolution2;
    private String evolution3;

    @Column(name = "dex_number")
    private Integer dexNumber;

    @Column(name = "evolution1image")
    private String evolution1Image;

    @Column(name = "evolution2image")
    private String evolution2Image;

    @Column(name = "evolution3image")
    private String evolution3Image;
    
    // Mega Evolution 1
    @Column(name = "mega1name")
    private String mega1Name;

    @Column(name = "mega1type1")
    private String mega1Type1;

    @Column(name = "mega1type2")
    private String mega1Type2;

    @Column(name = "mega1image")
    private String mega1Image;

    // Mega Evolution 2
    @Column(name = "mega2name")
    private String mega2Name;

    @Column(name = "mega2type1")
    private String mega2Type1;

    @Column(name = "mega2type2")
    private String mega2Type2;

    @Column(name = "mega2image")
    private String mega2Image;

    // Gigantamax Form
    @Column(name = "gmax_name")
    private String gmaxName;

    @Column(name = "gmax_type1")
    private String gmaxType1;

    @Column(name = "gmax_type2")
    private String gmaxType2;

    @Column(name = "gmax_image")
    private String gmaxImage;

    public Pokemon() {}

    // ==== GETTER & SETTER ====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType1() { return type1; }
    public void setType1(String type1) { this.type1 = type1; }

    public String getType2() { return type2; }
    public void setType2(String type2) { this.type2 = type2; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getEvolution1() { return evolution1; }
    public void setEvolution1(String evolution1) { this.evolution1 = evolution1; }

    public String getEvolution2() { return evolution2; }
    public void setEvolution2(String evolution2) { this.evolution2 = evolution2; }

    public String getEvolution3() { return evolution3; }
    public void setEvolution3(String evolution3) { this.evolution3 = evolution3; }

    public Integer getDexNumber() { return dexNumber; }
    public void setDexNumber(Integer dexNumber) { this.dexNumber = dexNumber; }

    public String getEvolution1Image() { return evolution1Image; }
    public void setEvolution1Image(String evolution1Image) { this.evolution1Image = evolution1Image; }

    public String getEvolution2Image() { return evolution2Image; }
    public void setEvolution2Image(String evolution2Image) { this.evolution2Image = evolution2Image; }

    public String getEvolution3Image() { return evolution3Image; }
    public void setEvolution3Image(String evolution3Image) { this.evolution3Image = evolution3Image; }

  public String getMega1Name() { return mega1Name; }
    public void setMega1Name(String mega1Name) { this.mega1Name = mega1Name; }

    public String getMega1Type1() { return mega1Type1; }
    public void setMega1Type1(String mega1Type1) { this.mega1Type1 = mega1Type1; }

    public String getMega1Type2() { return mega1Type2; }
    public void setMega1Type2(String mega1Type2) { this.mega1Type2 = mega1Type2; }

    public String getMega1Image() { return mega1Image; }
    public void setMega1Image(String mega1Image) { this.mega1Image = mega1Image; }

    public String getMega2Name() { return mega2Name; }
    public void setMega2Name(String mega2Name) { this.mega2Name = mega2Name; }

    public String getMega2Type1() { return mega2Type1; }
    public void setMega2Type1(String mega2Type1) { this.mega2Type1 = mega2Type1; }

    public String getMega2Type2() { return mega2Type2; }
    public void setMega2Type2(String mega2Type2) { this.mega2Type2 = mega2Type2; }

    public String getMega2Image() { return mega2Image; }
    public void setMega2Image(String mega2Image) { this.mega2Image = mega2Image; }

    public String getGmaxName() { return gmaxName; }
    public void setGmaxName(String gmaxName) { this.gmaxName = gmaxName; }

    public String getGmaxType1() { return gmaxType1; }
    public void setGmaxType1(String gmaxType1) { this.gmaxType1 = gmaxType1; }

    public String getGmaxType2() { return gmaxType2; }
    public void setGmaxType2(String gmaxType2) { this.gmaxType2 = gmaxType2; }

    public String getGmaxImage() { return gmaxImage; }
    public void setGmaxImage(String gmaxImage) { this.gmaxImage = gmaxImage; }

    
    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type1='" + type1 + '\'' +
                ", type2='" + type2 + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", evolution1='" + evolution1 + '\'' +
                ", evolution2='" + evolution2 + '\'' +
                ", evolution3='" + evolution3 + '\'' +
                ", dexNumber=" + dexNumber +
                ", evolution1Image='" + evolution1Image + '\'' +
                ", evolution2Image='" + evolution2Image + '\'' +
                ", evolution3Image='" + evolution3Image + '\'' +
               ", mega1Name='" + mega1Name + '\'' +
                ", mega1Type1='" + mega1Type1 + '\'' +
                ", mega1Type2='" + mega1Type2 + '\'' +
                ", mega1Image='" + mega1Image + '\'' +
                ", mega2Name='" + mega2Name + '\'' +
                ", mega2Type1='" + mega2Type1 + '\'' +
                ", mega2Type2='" + mega2Type2 + '\'' +
                ", mega2Image='" + mega2Image + '\'' +
                ", gmaxName='" + gmaxName + '\'' +
                ", gmaxType1='" + gmaxType1 + '\'' +
                ", gmaxType2='" + gmaxType2 + '\'' +
                ", gmaxImage='" + gmaxImage + '\'' +
                '}';
    }
}
