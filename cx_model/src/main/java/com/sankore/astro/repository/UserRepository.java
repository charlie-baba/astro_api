package com.sankore.astro.repository;

import com.sankore.astro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 15/05/2019
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAddress(String email);

    User findUserById(Long userId);

    List<User> findUsersByIdIn(List<Long> userIds);
	
	@Query("select u from User u left join fetch u.client c where u.id = :userId")
	User findUserAndClientById(@Param("userId") Long userId);
    
    User findUserByEmailAddressIgnoreCase(String email);

    boolean existsByEmailAddressIgnoreCaseAndClient_Code(String email, String clientCode);

    User findByEmailAddressAndActiveTrue(String email);

    @Query("select u from User u left join fetch u.client c where u.emailAddress = :email")
    User findByEmail(@Param("email") String email);

    @Query("select u from User u left join fetch u.client c where u.id = :userId and c.code = :clientCode")
    User findByIdAndClientCode(@Param("userId") Long userId, @Param("clientCode") String clientCode);

    List<User> findAllByClient_Code(String clientCode);

    @Query("select u from User u, IN(u.userRoles) as r where u.client.id = :clientId and r.name = :roleName")
    List<User> findUserByClientIdAndRole(@Param("clientId") Long clientId, @Param("roleName") String roleName);

    @Query("select u from User u left join fetch u.customPermissions p where u.id = :userId")
    User findUserAndPermissionsById(@Param("userId") Long id);
}
