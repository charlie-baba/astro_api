package com.sankore.astro.repository;

import com.sankore.astro.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 06/06/2019
 */
public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {

    IssueCategory findIssueCategoryByNameIgnoreCase(String name);

    List<IssueCategory> findAllByActiveTrue();

    List<IssueCategory> findIssueCategoriesByIdIn(List<Long> categoryIds);

    @Query("select distinct c from IssueCategory c left join fetch c.issueTypes t where c.active = true")
    List<IssueCategory> loadAllCategories();

    @Query("select c from IssueCategory c left join fetch c.issueTypes t where c.id = :categoryId")
    IssueCategory findIssueCategoryById(@Param("categoryId") Long categoryId);

    boolean existsByNameIgnoreCase(String categoryName);

}
