package com.sankore.astro.repository;

import com.sankore.astro.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 22/05/2019
 */
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Menu findMenuById(Long id);

    Menu findMenuByUrl(String url);

    List<Menu> findMenusByIdIn(List<Long> menuIds);

    @Query("select distinct r.menus from Role r where r.id in :roleIds")
    List<Menu> findByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Query("select distinct m from Role r left join r.menus m where r.name = :roleName and r.system = true and m.active = true order by m.position")
    List<Menu> findSystemMenusByRoleName(@Param("roleName") String roleName);
}
