package com.pokeworld.pokedex.sevices;

import com.pokeworld.pokedex.models.item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemApiService {

    @Autowired
    private ItemRepository itemRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // Lokasi folder penyimpanan gambar
    private final String uploadDir = System.getProperty("user.dir") + "/public/images/";

    // Ambil data dari PokeAPI sebagai Map
    public Map<String, Object> fetchItemData(String idOrName) {
        String url = "https://pokeapi.co/api/v2/item/" + idOrName;
        return restTemplate.getForObject(url, Map.class);
    }

    // Mapping dari Map ke entity item
    public item mapToItemEntity(Map<String, Object> data) {
        item newItem = new item();

        newItem.setName_item((String) data.get("name"));

        // Ambil effect dalam bahasa Inggris
        List<Map<String, Object>> effects = (List<Map<String, Object>>) data.get("effect_entries");
        if (effects != null) {
            for (Map<String, Object> effectEntry : effects) {
                Map<String, String> language = (Map<String, String>) effectEntry.get("language");
                if (language != null && "en".equals(language.get("name"))) {
                    newItem.setEffect((String) effectEntry.get("effect"));
                    break;
                }
            }
        }

        // Ambil kategori
        Map<String, String> category = (Map<String, String>) data.get("category");
        if (category != null) {
            newItem.setCategory(category.get("name"));
        }

        // Fling Power
        Object flingPowerObj = data.get("fling_power");
        if (flingPowerObj instanceof Number) {
            newItem.setFlingPower(((Number) flingPowerObj).intValue());
        }

        // Attributes
        List<Map<String, String>> attributes = (List<Map<String, String>>) data.get("attributes");
        if (attributes != null) {
            String attrStr = attributes.stream()
                    .map(attr -> attr.get("name"))
                    .collect(Collectors.joining(", "));
            newItem.setAttributes(attrStr);
        }

        // Gambar
        Map<String, Object> sprites = (Map<String, Object>) data.get("sprites");
        if (sprites != null && sprites.get("default") != null) {
            String imageUrl = sprites.get("default").toString();
            try {
                String savedImageName = downloadImage(imageUrl);
                newItem.setPicture(savedImageName); // simpan nama file lokal
            } catch (Exception e) {
                System.err.println("Gagal unduh gambar untuk item: " + newItem.getName_item());
                newItem.setPicture(null); // fallback
            }
        }

        return newItem;
    }

    // Fetch satu item dari API dan simpan ke DB
    public item fetchAndSaveItem(String idOrName) {
        Map<String, Object> data = fetchItemData(idOrName);
        if (data == null) return null;

        item newItem = mapToItemEntity(data);
        return itemRepository.save(newItem);
    }

    // Fetch semua item dari API dan simpan
    public void fetchAllAndSaveItems() {
        String url = "https://pokeapi.co/api/v2/item?limit=1000";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) return;

        List<Map<String, String>> results = (List<Map<String, String>>) response.get("results");
        if (results == null) return;

        for (Map<String, String> result : results) {
            String name = result.get("name");
            try {
                fetchAndSaveItem(name);
            } catch (Exception e) {
                System.err.println("Gagal mengambil item: " + name);
            }
        }
    }

    // Helper: unduh gambar dari URL dan simpan ke /public/images/
    private String downloadImage(String imageUrl) throws Exception {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        String extension = imageUrl.substring(imageUrl.lastIndexOf('.'));
        String filename = UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }

        return filename; // hanya nama file-nya yang akan disimpan ke DB
    }
}
