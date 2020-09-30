package com.sankore.astro.repository;

import com.sankore.astro.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Obi on 23/05/2019
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByMenu_Id(Long menuId);

    List<Permission> findAllByRole_Id(Long roleId);

    List<Permission> findAllByRole_IdIn(List<Long> roleIds);

    List<Permission> findAllByUser_Id(Long userId);
}
