package com.sankore.server.emailreader.repository;

import com.sankore.server.emailreader.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Obi on 10/05/2019
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

}
