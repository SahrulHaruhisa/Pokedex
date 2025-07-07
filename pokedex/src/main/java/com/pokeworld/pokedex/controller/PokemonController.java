package com.pokeworld.pokedex.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeworld.pokedex.models.Pokemon;
import com.pokeworld.pokedex.repository.PokemonRepository;
import com.pokeworld.pokedex.sevices.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/pokemon")
public class PokemonController {

    @Autowired
    private PokemonRepository repo;

    @Autowired
    private PokemonService service;

    @GetMapping
    @ResponseBody
    public List<Pokemon> getAllPokemon() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseBody
    public Pokemon createPokemon(@RequestBody Pokemon pokemon) {
        return repo.save(pokemon);
    }
  @GetMapping("/fetch-mega-gigamax")
@ResponseBody
public String fetchMegaAndGigamaxData() {
    System.out.println("Mulai fetch data Mega Evolution dan Gigantamax...");
    try {
        service.loadMegaAndGmaxFromJsonFile();
        return "Data Mega Evolution dan Gigantamax berhasil diambil!";
    } catch (Exception e) {
        e.printStackTrace();
        return "Gagal mengambil data Mega Evolution dan Gigantamax: " + e.getMessage();
    }
}
    @GetMapping("/update-dex")
    public String updateDexNumberOnly() {
        service.updateDexNumbersOnly();
        return "redirect:/pokemon/list";
    }

    @GetMapping("/fetch-all")
@ResponseBody
public String fetchAllPokemon() throws Exception {
    boolean hasError = service.fetchAndSaveAllPokemon();

    if (hasError) {
        return "⚠️ Beberapa data Pokémon gagal diambil atau disimpan. Silakan cek log untuk detailnya.";
    } else {
        return "✅ Semua data Pokémon (tanpa Mega Evolution dan Gigantamax) berhasil diambil dan disimpan!";
    }
}


    @GetMapping("/fetch/{name}")
    @ResponseBody
    public Pokemon fetchAndSaveFromApi(@PathVariable String name) throws Exception {
        if (repo.existsByNameIgnoreCase(name)) {
            return repo.findByNameIgnoreCase(name);
        }

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String url = "https://pokeapi.co/api/v2/pokemon/" + name.toLowerCase();
        String json = restTemplate.getForObject(url, String.class);
        JsonNode root = mapper.readTree(json);

        String type1 = root.get("types").get(0).get("type").get("name").asText();
        String type2 = (root.get("types").size() > 1)
                ? root.get("types").get(1).get("type").get("name").asText()
                : null;

        String imageUrl = root.get("sprites").get("front_default").asText();

        Pokemon p = new Pokemon();
        p.setName(name.toLowerCase());
        p.setType1(type1);
        p.setType2(type2);
        p.setImageUrl(imageUrl);

        return repo.save(p);
    }

  


    @GetMapping("/list")
    public String showPokemonList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());
        Page<Pokemon> pokemonPage;

        if (keyword != null && !keyword.isBlank()) {
            pokemonPage = repo.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            pokemonPage = repo.findAll(pageable);
        }

        long totalCount = repo.count();
        model.addAttribute("pokemonPage", pokemonPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pokemonPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("isSearching", keyword != null && !keyword.isBlank());

        return "pokemon/pokemon-list";
    }

    @GetMapping("/grid")
    public String showPokemonGridPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, 16, Sort.by("dexNumber").ascending());
        Page<Pokemon> pokemonPage;

        if (keyword != null && !keyword.isBlank()) {
            pokemonPage = repo.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            pokemonPage = repo.findAll(pageable);
        }

        model.addAttribute("pokemonPage", pokemonPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pokemonPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "pokedex/pokemon";
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public Pokemon getPokemonDetail(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @GetMapping("/{id:[0-9]+}")
    @ResponseBody
    public Pokemon getPokemonById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }
} 
