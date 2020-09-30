package com.sankore.astro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sankore.astro.entity.UserGroupMapping;

/**
 * @author Obi on 26/03/2020
 */
public interface UserGroupRepository extends JpaRepository<UserGroupMapping, Long> {

}
