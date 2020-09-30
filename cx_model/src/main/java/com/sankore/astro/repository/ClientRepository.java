package com.sankore.astro.repository;

import com.sankore.astro.entity.Client;
import com.sankore.astro.enums.ManagementInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Obi on 10/05/2019
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findClientById(Long clientId);

    Client findByCode(String code);

    boolean existsByCode(String code);

    @Query("select c from Client c where c.code <> '"+ ManagementInfo.CLIENT_SERVICE_CODE + "' ")
    List<Client> findValidClients();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByShortNameIgnoreCase(String shortName);

    boolean existsByEmail(String email);
}
