package com.sankore.astro.services;

import com.sankore.astro.entity.IssueCategory;
import com.sankore.astro.entity.IssueType;
import com.sankore.astro.pojo.Category;
import com.sankore.astro.pojo.IssueTypePojo;
import com.sankore.astro.request.issuecategory.IssueCategoryRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Obi on 06/06/2019
 */
public interface IssueCategoryService {

    void fetchIssueCategoryDetails(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchIssueCategories(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void loadIssueCategories(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void createIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void editIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler);

    List<Category> toCategories(Collection<IssueCategory> issueCategories, boolean fetchIssueTypes);

    Category toCategory(IssueCategory issueCategory, boolean fetchIssueTypes);

    List<IssueTypePojo> toIssueTypes(Collection<IssueType> issueTypes);

    IssueTypePojo toIssueType(IssueType issueType);

    Set<IssueCategory> fromCategories(Collection<Category> categories);
}
