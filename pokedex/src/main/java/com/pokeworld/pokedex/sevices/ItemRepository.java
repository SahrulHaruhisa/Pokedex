package com.pokeworld.pokedex.sevices;

import com.pokeworld.pokedex.models.item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<item, Integer> {

    // Cari berdasarkan nama
    @Query("SELECT i FROM item i WHERE LOWER(i.name_item) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<item> searchByName(@Param("keyword") String keyword, Pageable pageable);

    // Cari berdasarkan kategori
    Page<item> findByCategory(String category, Pageable pageable);

    // Cari berdasarkan nama DAN kategori
    @Query("SELECT i FROM item i WHERE LOWER(i.name_item) LIKE LOWER(CONCAT('%', :keyword, '%')) AND i.category = :category")
    Page<item> searchByNameAndCategory(@Param("keyword") String keyword,
                                       @Param("category") String category,
                                       Pageable pageable);

    // Ambil daftar kategori unik
    @Query("SELECT DISTINCT i.category FROM item i WHERE i.category IS NOT NULL")
    List<String> findDistinctCategories();
    @Query(value = "SELECT * FROM items ORDER BY name_item ASC", nativeQuery = true)
Page<item> findAllOrderByNameItem(Pageable pageable);
    // Optional: untuk jaga-jaga, biar tidak hilang
    Page<item> findAll(Pageable pageable);
}
