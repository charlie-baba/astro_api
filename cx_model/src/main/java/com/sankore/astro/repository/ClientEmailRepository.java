package com.sankore.astro.repository;

import com.sankore.astro.entity.ClientEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Obi on 28/05/2019
 */
public interface ClientEmailRepository extends JpaRepository<ClientEmail, Long> {

    boolean existsByEmailAddress(String email);

    ClientEmail findClientEmailByEmailAddress(String email);

    ClientEmail findClientEmailById(Long id);

    List<ClientEmail> findAllByActiveTrue();

    List<ClientEmail> findAllByClient_CodeAndActiveTrue(String clientCode);
    
    List<ClientEmail> findAllByClient_Code(String clientCode);
}
