package com.okta.examples.apiamhooks.repository;

import com.okta.examples.apiamhooks.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RepositoryRestResource
public interface BeerRepository extends JpaRepository<Beer, Long> {

    Beer findByName(String name);
}