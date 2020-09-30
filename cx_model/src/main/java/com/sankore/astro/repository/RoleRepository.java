package com.sankore.astro.repository;

import com.sankore.astro.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 22/05/2019
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role as r left join fetch r.permissions p where r.id = :id")
    Role findRoleById(@Param("id") Long id);

    List<Role> findRolesByIdIn(List<Long> roleIds);

    Role findRoleByName(String roleName);

    boolean existsByNameIgnoreCaseAndClient_Code(String roleName, String code);

    @Query(nativeQuery=true, value = "select r.id, r.name, r.description, r.is_active, r.is_system, r.client_fk, " +
            "(select count(u.id) from users as u where u.client_fk=r.client_fk and u.id in " +
            "(select m.user_id from user_role_mapping as m where m.role_id=r.id)) as usercount from roles as r, User as u " +
            "where r.client_fk = :clientId")
    List<Object[]> findRolesAndUserCountByClientId(@Param("clientId") Long clientId);
}
