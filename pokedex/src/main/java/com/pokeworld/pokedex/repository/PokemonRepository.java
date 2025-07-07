package com.pokeworld.pokedex.repository;

import com.pokeworld.pokedex.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
   boolean existsByNameIgnoreCase(String name);
Pokemon findByNameIgnoreCase(String name);
     
    Page<Pokemon> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
