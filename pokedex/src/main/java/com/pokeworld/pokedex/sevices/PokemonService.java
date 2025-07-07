package com.pokeworld.pokedex.sevices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokeworld.pokedex.models.Pokemon;
import com.pokeworld.pokedex.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository repo;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

 public boolean fetchAndSaveAllPokemon() throws Exception {
    boolean hasError = false;

    String url = "https://pokeapi.co/api/v2/pokemon?limit=100000";
    String jsonList = restTemplate.getForObject(url, String.class);
    JsonNode root = mapper.readTree(jsonList);
    JsonNode results = root.get("results");

    for (JsonNode node : results) {
        String name = node.get("name").asText();
        try {
            fetchAndSaveSinglePokemon(name);
            Thread.sleep(200);
        } catch (Exception e) {
            hasError = true;
            System.out.println("❌ Gagal fetch: " + name);
        }
    }

    return hasError;
}


    private void fetchAndSaveSinglePokemon(String name) {
        try {
            Pokemon existing = repo.findByNameIgnoreCase(name);

            String url = "https://pokeapi.co/api/v2/pokemon/" + name.toLowerCase();
            String json = restTemplate.getForObject(url, String.class);
            JsonNode data = mapper.readTree(json);

            String imageUrl = data.get("sprites").get("front_default").asText();

            JsonNode types = data.get("types");
            String type1 = types.get(0).get("type").get("name").asText();
            String type2 = (types.size() > 1) ? types.get(1).get("type").get("name").asText() : null;

            String speciesUrl = data.get("species").get("url").asText();
            String speciesJson = restTemplate.getForObject(speciesUrl, String.class);
            JsonNode speciesData = mapper.readTree(speciesJson);
            String evoChainUrl = speciesData.get("evolution_chain").get("url").asText();

            String evoJson = restTemplate.getForObject(evoChainUrl, String.class);
            JsonNode evoData = mapper.readTree(evoJson);
            JsonNode chain = evoData.get("chain");

            String evo1 = chain.get("species").get("name").asText();
            String evo2 = null;
            String evo3 = null;

            if (chain.has("evolves_to") && chain.get("evolves_to").size() > 0) {
                evo2 = chain.get("evolves_to").get(0).get("species").get("name").asText();
                if (chain.get("evolves_to").get(0).has("evolves_to") &&
                    chain.get("evolves_to").get(0).get("evolves_to").size() > 0) {
                    evo3 = chain.get("evolves_to").get(0).get("evolves_to").get(0).get("species").get("name").asText();
                }
            }

            String evo1Image = getImageForPokemon(evo1);
            String evo2Image = (evo2 != null) ? getImageForPokemon(evo2) : null;
            String evo3Image = (evo3 != null) ? getImageForPokemon(evo3) : null;

            if (existing != null) {
                existing.setType1(type1);
                existing.setType2(type2);
                existing.setImageUrl(imageUrl);
                existing.setEvolution1(evo1);
                existing.setEvolution2(evo2);
                existing.setEvolution3(evo3);
                existing.setEvolution1Image(evo1Image);
                existing.setEvolution2Image(evo2Image);
                existing.setEvolution3Image(evo3Image);
                repo.save(existing);
            } else {
                Pokemon p = new Pokemon();
                p.setName(name.toLowerCase());
                p.setType1(type1);
                p.setType2(type2);
                p.setImageUrl(imageUrl);
                p.setEvolution1(evo1);
                p.setEvolution2(evo2);
                p.setEvolution3(evo3);
                p.setEvolution1Image(evo1Image);
                p.setEvolution2Image(evo2Image);
                p.setEvolution3Image(evo3Image);
                repo.save(p);
            }

        } catch (Exception e) {
            System.out.println("\u274C Gagal ambil data untuk: " + name + " | " + e.getMessage());
        }
    }

    private String getImageForPokemon(String name) {
        try {
            String url = "https://pokeapi.co/api/v2/pokemon/" + name.toLowerCase();
            String json = restTemplate.getForObject(url, String.class);
            JsonNode data = mapper.readTree(json);
            return data.get("sprites").get("front_default").asText();
        } catch (Exception e) {
            System.out.println("\u26A0\uFE0F Gagal ambil gambar untuk: " + name);
            return null;
        }
    }

    public void updateDexNumbersOnly() {
        List<Pokemon> allPokemons = repo.findAll();
        for (Pokemon p : allPokemons) {
            try {
                String name = p.getName().toLowerCase();
                String url = "https://pokeapi.co/api/v2/pokemon/" + name;
                String json = restTemplate.getForObject(url, String.class);
                JsonNode data = mapper.readTree(json);

                int dexNumber = data.get("id").asInt();
                p.setDexNumber(dexNumber);
                repo.save(p);
                System.out.println("\u2714\uFE0F Updated: " + name + " -> #" + dexNumber);
                Thread.sleep(100);

            } catch (Exception e) {
                System.out.println("\u274C Gagal update dexNumber untuk " + p.getName() + ": " + e.getMessage());
            }
        }
    }
    private void applyTypesFromFormName(Pokemon p, String formName, boolean isMega1, boolean isMega2) {
        try {
            String formattedName = formName.toLowerCase()
                .replace(" ", "-")
                .replace("é", "e")
                .replace(".", "")
                .replace("’", "")
                .replace("'", "")
                .replace(":", "")
                .replace("_", "-");

            if (formattedName.endsWith("-gmax")) {
                formattedName = formattedName.replace("-gmax", "") + "-gmax";
            }

            String url = "https://pokeapi.co/api/v2/pokemon/" + formattedName;
            String json = restTemplate.getForObject(url, String.class);
            JsonNode data = mapper.readTree(json);

            JsonNode types = data.get("types");
            if (types == null || types.isEmpty()) {
                System.out.println("⚠️ Tidak ada type untuk: " + formName);
                return;
            }

            String type1 = types.get(0).get("type").get("name").asText();
            String type2 = (types.size() > 1) ? types.get(1).get("type").get("name").asText() : null;

            if (isMega1) {
                p.setMega1Type1(type1);
                p.setMega1Type2(type2);
            } else if (isMega2) {
                p.setMega2Type1(type1);
                p.setMega2Type2(type2);
            } else {
                p.setGmaxType1(type1);
                p.setGmaxType2(type2);
            }

        } catch (Exception e) {
            System.out.println("❌ Gagal ambil type dari form: " + formName + " -> " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void loadMegaAndGmaxFromJsonFile() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/pokemon_mega_gigamax.json");
            JsonNode root = mapper.readTree(inputStream);

            JsonNode megaArray = root.get("mega");
            for (JsonNode entry : megaArray) {
                String baseName = entry.get("base_name").asText();
                Pokemon p = repo.findByNameIgnoreCase(baseName);
                if (p == null) continue;

                JsonNode mega1 = entry.get("mega1");
                if (mega1 != null) {
                    p.setMega1Name(mega1.get("name").asText());
                    p.setMega1Type1(mega1.get("type1").asText());
                    p.setMega1Type2(mega1.get("type2").asText(null));
                    p.setMega1Image(mega1.get("image_url").asText());
                }

                JsonNode mega2 = entry.get("mega2");
                if (mega2 != null) {
                    p.setMega2Name(mega2.get("name").asText());
                    p.setMega2Type1(mega2.get("type1").asText());
                    p.setMega2Type2(mega2.get("type2").asText(null));
                    p.setMega2Image(mega2.get("image_url").asText());
                }

                repo.save(p);
            }

            JsonNode gmaxArray = root.get("gigantamax");
            for (JsonNode entry : gmaxArray) {
                String baseName = entry.get("base_name").asText();
                Pokemon p = repo.findByNameIgnoreCase(baseName);
                if (p == null) continue;

                p.setGmaxName(entry.get("name").asText());
                p.setGmaxType1(entry.get("type1").asText());
                p.setGmaxType2(entry.get("type2").asText(null));
                p.setGmaxImage(entry.get("image_url").asText());

                repo.save(p);
            }

            System.out.println("✅ Berhasil memuat Mega & Gmax dari JSON lokal.");
        } catch (Exception e) {
            System.out.println("❌ Gagal membaca file JSON lokal: " + e.getMessage());
        }
    }

    

}