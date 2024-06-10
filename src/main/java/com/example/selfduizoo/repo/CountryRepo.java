package com.example.selfduizoo.repo;

import com.example.selfduizoo.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CountryRepo extends JpaRepository<Country, Long> {
    //국가명으로 국가 entity 가져오기
    Optional<Country> findByCountryName(String countryName);
    
}
