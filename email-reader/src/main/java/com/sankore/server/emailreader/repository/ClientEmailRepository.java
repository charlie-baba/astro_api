package com.sankore.server.emailreader.repository;

import com.sankore.server.emailreader.entity.ClientEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Obi on 28/05/2019
 */
public interface ClientEmailRepository extends JpaRepository<ClientEmail, Long> {

    List<ClientEmail> findAllByActiveTrue();

    List<ClientEmail> findAllByIdAndActiveTrue(Long id);
}
