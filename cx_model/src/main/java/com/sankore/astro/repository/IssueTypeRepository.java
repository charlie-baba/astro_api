package com.sankore.astro.repository;

import com.sankore.astro.entity.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Obi on 06/06/2019
 */
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {

    List<IssueType> findAllByActiveTrue();

    List<IssueType> findAllByIssueCategory_Id(Long categoryId);

    IssueType findIssueTypeByNameIgnoreCase(String name);

    IssueType findIssueTypeById(Long id);

    boolean existsByNameIgnoreCaseAndIssueCategory_Id(String name, Long categoryId);

    boolean existsByNameIgnoreCase(String name);
}
