package com.sankore.astro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sankore.astro.entity.Group;
import com.sankore.astro.entity.UserGroupMapping;

/**
 * @author Obi on 28/05/2019
 */
public interface GroupRepository extends JpaRepository<Group, Long> {
	
	Group findGroupByNameIgnoreCaseAndClient_Code(String name, String clientCode);
	
	Group findGroupById(Long groupId);
	
	@Query("select g from Group g left join fetch g.userGroupMapping m where g.id = :groupId")
    Group findGroupAndUserMappingsById(@Param("groupId") Long id);
	
	List<Group> findAllByClient_Code(String clientCode);
	
	@Query("select g.id, g.name, g.description, g.active, " +
           "(select count(u.id) from UserGroupMapping u where u.group.id = g.id) from Group g where g.client.code = :clientCode order by g.id desc")
    List<Object[]> findAllByClientCode(@Param("clientCode") String clientCode);

	@Query("select g from Group g left join fetch g.issueCategories c where g.id = :groupId and g.client.code = :clientCode")
	Group findByIdAndClientCode(@Param("groupId") Long groupId, @Param("clientCode") String clientCode);
	
	@Query("select g from UserGroupMapping g left join fetch g.user u where g.group.id = :groupId order by g.id desc")
	List<UserGroupMapping> findAllUsersByGroup(@Param("groupId") Long groupId);

	@Query("select m.group.id from UserGroupMapping m where m.user.id = :userId order by m.id desc")
	List<Long> findGroupsByUserId(@Param("userId") Long userId);
}
