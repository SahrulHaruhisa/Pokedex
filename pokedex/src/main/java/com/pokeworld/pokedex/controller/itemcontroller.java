package com.pokeworld.pokedex.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import com.pokeworld.pokedex.models.item;
import com.pokeworld.pokedex.models.itemsDto;
import com.pokeworld.pokedex.sevices.ItemApiService;
import com.pokeworld.pokedex.sevices.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/items")
public class itemcontroller {

    @Autowired
    private ItemRepository repo;

    @Autowired
    private ItemApiService itemApiService;

    // Absolute path folder gambar
    private final String uploadDir = System.getProperty("user.dir") + "/public/images/";

    // Halaman utama
    @GetMapping({"", "/"})
public String showItemList(Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "5") int size,
                           @RequestParam(value = "keyword", required = false) String keyword,
                           @RequestParam(value = "category", required = false) String category) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    Page<item> itemPage;

    if ((keyword != null && !keyword.isEmpty()) && (category != null && !category.isEmpty())) {
        itemPage = repo.searchByNameAndCategory(keyword, category, pageable);
    } else if (keyword != null && !keyword.isEmpty()) {
        itemPage = repo.searchByName(keyword, pageable);
    } else if (category != null && !category.isEmpty()) {
        itemPage = repo.findByCategory(category, pageable);
    } else {
        itemPage = repo.findAll(pageable);
    }

    model.addAttribute("items", itemPage.getContent());
    model.addAttribute("currentPage", itemPage.getNumber());
    model.addAttribute("totalPages", itemPage.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("selectedCategory", category);

    // Ambil daftar kategori unik untuk filter
    List<String> categories = repo.findDistinctCategories();
    model.addAttribute("categoryList", categories);

    return "items/index";
}

    @GetMapping("/create")
    public String ShowCreatePage(Model model) {
        model.addAttribute("itemsDto", new itemsDto());
        return "items/CreateItems";
    }

    @PostMapping("/create")
    public String createItem(@Valid @ModelAttribute("itemsDto") itemsDto itemsDto,
                             BindingResult result,
                             Model model) throws IOException {

        if (itemsDto.getPicture() == null || itemsDto.getPicture().isEmpty()) {
            result.addError(new FieldError("itemsDto", "picture", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "items/CreateItems";
        }

        MultipartFile image = itemsDto.getPicture();
        String uniqueFileName = generateUniqueFileName(image.getOriginalFilename());

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        item item = new item();
        item.setName_item(itemsDto.getName_item());
        item.setEffect(itemsDto.getEffect());
        item.setPicture(uniqueFileName);
        item.setCategory(itemsDto.getCategory());
        item.setFlingPower(itemsDto.getFlingPower());
        item.setAttributes(itemsDto.getAttributes());

        repo.save(item);

        return "redirect:/items";
    }

    @PostMapping("/update")
    public String updateItem(@ModelAttribute("itemsDto") itemsDto itemsDto) throws IOException {
        item existingItem = repo.findById(itemsDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));

        existingItem.setName_item(itemsDto.getName_item());
        existingItem.setEffect(itemsDto.getEffect());
        existingItem.setCategory(itemsDto.getCategory());
        existingItem.setFlingPower(itemsDto.getFlingPower());
        existingItem.setAttributes(itemsDto.getAttributes());

        MultipartFile newImage = itemsDto.getPicture();
        if (newImage != null && !newImage.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);

            String oldFile = itemsDto.getOldPicture();
            if (oldFile != null && !oldFile.isEmpty()) {
                Files.deleteIfExists(uploadPath.resolve(oldFile));
            }

            String uniqueFileName = generateUniqueFileName(newImage.getOriginalFilename());
            Files.copy(newImage.getInputStream(), uploadPath.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
            existingItem.setPicture(uniqueFileName);
        }

        repo.save(existingItem);
        return "redirect:/items";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable("id") int id) throws IOException {
        item existingItem = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));

        if (existingItem.getPicture() != null) {
            Path imagePath = Paths.get(uploadDir).resolve(existingItem.getPicture());
            Files.deleteIfExists(imagePath);
        }

        repo.deleteById(id);
        return "redirect:/items";
    }

    // --- API JSON ---

    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<item> getAllItemsApi() {
        return repo.findAll();
    }

    @GetMapping(value = "/api/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public item getItemByIdApi(@PathVariable int id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
    }

    @RequestMapping(value = "/api/fetchAll", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> fetchAllItems() {
        try {
            itemApiService.fetchAllAndSaveItems();
            return ResponseEntity.ok("All items fetched and saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch items.");
        }
    }

    @PostMapping(value = "/api/fetch/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> fetchAndSaveFromPokeApi(@PathVariable String idOrName) {
        item newItem = itemApiService.fetchAndSaveItem(idOrName);
        if (newItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found in PokeAPI");
        }

        try {
            String localImageName = downloadImageFromUrl(newItem.getPicture());
            if (localImageName != null) {
                newItem.setPicture(localImageName);
            } else {
                newItem.setPicture(null);
            }

            repo.save(newItem);
            return ResponseEntity.ok(newItem);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download image.");
        }
    }

    @DeleteMapping(value = "/api/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteItemApi(@PathVariable int id) throws IOException {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with ID " + id + " not found");
        }

        item existingItem = repo.findById(id).get();
        if (existingItem.getPicture() != null) {
            Path imagePath = Paths.get(uploadDir).resolve(existingItem.getPicture());
            Files.deleteIfExists(imagePath);
        }

        repo.deleteById(id);
        return ResponseEntity.ok("Item with ID " + id + " deleted");
    }

    // --- Helper methods ---

    private String generateUniqueFileName(String originalName) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String downloadImageFromUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) return null;
        String extension = imageUrl.substring(imageUrl.lastIndexOf('.'));
        String filename = UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }

        return filename;
    }
    // Endpoint untuk halaman grid user (public view)
@GetMapping("/gridz")
public String showItemGridPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        Model model) {

    Pageable pageable = PageRequest.of(page, 5); // TANPA sorting

    Page<item> itemPage;

    if (keyword != null && !keyword.isBlank() && category != null && !category.isBlank()) {
        itemPage = repo.searchByNameAndCategory(keyword, category, pageable);
    } else if (keyword != null && !keyword.isBlank()) {
        itemPage = repo.searchByName(keyword, pageable);
    } else if (category != null && !category.isBlank()) {
        itemPage = repo.findByCategory(category, pageable);
    } else {
        itemPage = repo.findAllOrderByNameItem(pageable); // GANTI DI SINI
    }

    model.addAttribute("itemPage", itemPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", itemPage.getTotalPages());
    model.addAttribute("keyword", keyword);
    model.addAttribute("selectedCategory", category);
    model.addAttribute("categoryList", repo.findDistinctCategories());

    return "pokedex/items";
}


// Endpoint JSON detail untuk modal
@GetMapping("/detail/{id}")
@ResponseBody
public item getItemDetail(@PathVariable Integer id) {
    return repo.findById(id).orElse(null);
}

}
