package com.sankore.astro.services.impl;

import com.sankore.astro.entity.IssueCategory;
import com.sankore.astro.entity.IssueType;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.Category;
import com.sankore.astro.pojo.IssueTypePojo;
import com.sankore.astro.repository.IssueCategoryRepository;
import com.sankore.astro.repository.IssueTypeRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.issuecategory.IssueCategoryRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.issuecategory.IssueCategoryListResponse;
import com.sankore.astro.response.issuecategory.IssueCategoryResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.IssueCategoryService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Obi on 06/06/2019
 */
@Service
public class IssueCategoryServiceImpl implements IssueCategoryService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    IssueTypeRepository issueTypeRepository;

    @Autowired
    IssueCategoryRepository issueCategoryRepository;

    @Autowired
    I18nMessagingService messagingService;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchIssueCategories.name().equals(handlerMethod))
            fetchIssueCategories((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.loadIssueCategories.name().equals(handlerMethod))
            loadIssueCategories((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.createIssueCategory.name().equals(handlerMethod))
            createIssueCategory((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.editIssueCategory.name().equals(handlerMethod))
            editIssueCategory((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchIssueCategoryDetails.name().equals(handlerMethod))
            fetchIssueCategoryDetails((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteIssueCategory.name().equals(handlerMethod))
            deleteIssueCategory((IssueCategoryRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Not_Found)));
    }

    @Override
    public void fetchIssueCategoryDetails(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        IssueCategoryResponse response = new IssueCategoryResponse(ResponseCode.Success);
        try {
            IssueCategory issueCategory = issueCategoryRepository.findIssueCategoryById(request.getCategory().getId());
            response.setCategory(toCategory(issueCategory, true));
        } catch (Exception e) {
            log.error("Error", e);
            response = new IssueCategoryResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchIssueCategories(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        IssueCategoryListResponse response = new IssueCategoryListResponse(ResponseCode.Success);
        try {
            List<IssueCategory> issueCategories = issueCategoryRepository.findAllByActiveTrue();
            List<Category> categories = toCategories(issueCategories, false);
            response.setCategories(categories);
        } catch (Exception e) {
            log.error("Error", e);
            response = new IssueCategoryListResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void loadIssueCategories(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        IssueCategoryListResponse response = new IssueCategoryListResponse(ResponseCode.Success);
        try {
            List<IssueCategory> issueCategories = issueCategoryRepository.loadAllCategories();
            List<Category> categories = toCategories(issueCategories, true);
            response.setCategories(categories);
        } catch (Exception e) {
            log.error("Error", e);
            response = new IssueCategoryListResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void createIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Bad_Request);
        try {
            if (issueCategoryRepository.existsByNameIgnoreCase(request.getCategory().getName())) {
                response.setResponseMessage(messagingService.getMessage("issue_category_name_exists"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            IssueCategory category = new IssueCategory();
            category.setName(request.getCategory().getName().trim());
            category.setDescription(request.getCategory().getDescription());
            issueCategoryRepository.save(category);

            if (!CollectionUtils.isEmpty(request.getIssueTypes())) {
                Set<IssueType> issueTypeSet = new HashSet<>();
                for (String issueTypeName : request.getIssueTypes()) {
                    issueTypeName = issueTypeName.trim();
                    if (issueTypeRepository.existsByNameIgnoreCase(issueTypeName)) {
                        response.setResponseMessage(messagingService.getMessage("issue_type_name_exists", new String[] { issueTypeName }));
                        handler.handle(Future.succeededFuture(response));
                        return;
                    }

                    IssueType issueType = new IssueType();
                    issueType.setName(issueTypeName);
                    issueType.setDescription(issueTypeName);
                    issueType.setIssueCategory(category);
                    issueTypeSet.add(issueType);
                }
                issueTypeRepository.saveAll(issueTypeSet);
            }
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("issue_category_save_successful"));
        } catch (Exception e) {
            log.error("Error", e);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void editIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Bad_Request);
        IssueCategory category = issueCategoryRepository.findIssueCategoryById(request.getCategory().getId());
        if (category == null) {
            response.setResponseMessage(messagingService.getMessage("issue_category_not_found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }
        String categoryName = request.getCategory().getName().trim();
        if (issueCategoryRepository.existsByNameIgnoreCase(categoryName) && !categoryName.equalsIgnoreCase(category.getName())) {
            response.setResponseMessage(messagingService.getMessage("issue_category_name_exists"));
            handler.handle(Future.succeededFuture(response));
            return;
        }
        category.setName(categoryName);
        category.setDescription(request.getCategory().getDescription());
        issueCategoryRepository.save(category);

        //Update existing issue types
        List<IssueType> issueTypeList = new ArrayList<>();
        for (IssueType issueType: category.getIssueTypes()) {
            IssueTypePojo issueTypePojo = request.getCategory().getIssueTypes().stream().filter(x ->
                    issueType.getId().equals(x.getId())).findAny().orElse(null);
            if (issueTypePojo != null) {
                String issueTypeName = issueTypePojo.getName().trim();
                IssueType existingIssueType = issueTypeRepository.findIssueTypeByNameIgnoreCase(issueTypeName);
                if (existingIssueType != null && !existingIssueType.getId().equals(issueType.getId())) {
                    response.setResponseMessage(messagingService.getMessage("issue_type_name_exists", new String[] { issueTypeName }));
                    handler.handle(Future.succeededFuture(response));
                    return;
                }
                addIssueTypeToList(category, issueTypeList, issueTypeName, issueType);
            }
        }

        // add new issue types
        if (!CollectionUtils.isEmpty(request.getIssueTypes())) {
            for (String issueTypeName : request.getIssueTypes()) {
                if (issueTypeRepository.existsByNameIgnoreCase(issueTypeName)) {
                    response.setResponseMessage(messagingService.getMessage("issue_type_name_exists", new String[] { issueTypeName }));
                    handler.handle(Future.succeededFuture(response));
                    return;
                }
                IssueType issueType = new IssueType();
                addIssueTypeToList(category, issueTypeList, issueTypeName, issueType);
            }
        }
        issueTypeRepository.saveAll(issueTypeList);

        //Delete removed issue types that are not attached to any ticket sla
        int constraintCount = 0;
        for (IssueType issueType : category.getIssueTypes()) {
            if (!issueTypeList.contains(issueType)) {
                try {
                    log.info("deleting: "+ issueType.getName());
                    issueTypeRepository.delete(issueType);
                } catch (DataIntegrityViolationException e) {
                    constraintCount++;
                }
            }
        }
        if (constraintCount > 0) {
            response = new BaseResponse(ResponseCode.Info.getCode(), messagingService.getMessage("issue_type_constraints_exists"));
            handler.handle(Future.succeededFuture(response));
            return;
        }
        response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("issue_category_save_successful"));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void deleteIssueCategory(IssueCategoryRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            IssueCategory category = issueCategoryRepository.findIssueCategoryById(request.getCategory().getId());
            if (category == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("issue_category_not_found"));
            } else {
                issueCategoryRepository.delete(category);
                response = new BaseResponse(ResponseCode.Success);
            }
            issueCategoryRepository.delete(category);
        } catch (DataIntegrityViolationException dIVEx) {
            log.error("Constraint Violation Exception", dIVEx);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("category_attached_to_issue_type"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    private void addIssueTypeToList(IssueCategory category, List<IssueType> issueTypeList,
                                    String issueTypeName, IssueType issueType) {
        issueType.setName(issueTypeName);
        issueType.setDescription(issueTypeName);
        issueType.setIssueCategory(category);
        issueTypeList.add(issueType);
    }

    @Override
    public List<Category> toCategories(Collection<IssueCategory> issueCategories, boolean fetchIssueTypes) {
        List<Category> categories = new ArrayList<>();
        if (!CollectionUtils.isEmpty(issueCategories)) {
            for (IssueCategory issueCategory : issueCategories)
                categories.add(toCategory(issueCategory, fetchIssueTypes));
        }
        return categories;
    }

    @Override
    public Category toCategory(IssueCategory issueCategory, boolean fetchIssueTypes) {
        if (issueCategory == null)
            return null;

        Category category = new Category();
        category.setId(issueCategory.getId());
        category.setName(issueCategory.getName());
        category.setDescription(issueCategory.getDescription());
        if (fetchIssueTypes)
            category.setIssueTypes(toIssueTypes(issueCategory.getIssueTypes()));
        return category;
    }

    @Override
    public List<IssueTypePojo> toIssueTypes(Collection<IssueType> issueTypes) {
        List<IssueTypePojo> issueTypePojos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(issueTypes)) {
            for (IssueType issueType : issueTypes) {
                issueTypePojos.add(toIssueType(issueType));
            }
        }
        return issueTypePojos;
    }

    @Override
    public IssueTypePojo toIssueType(IssueType issueType) {
        if (issueType == null)
            return null;

        IssueTypePojo pojo = new IssueTypePojo();
        pojo.setId(issueType.getId());
        pojo.setName(issueType.getName());
        pojo.setDescription(issueType.getDescription());
        pojo.setCategoryId(issueType.getIssueCategory().getId());
        return pojo;
    }

    @Override
    public Set<IssueCategory> fromCategories(Collection<Category> categories) {
        if (CollectionUtils.isEmpty(categories))
            return null;

        List<Long> categoryIds = categories.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<IssueCategory> issueCategories = issueCategoryRepository.findIssueCategoriesByIdIn(categoryIds);
        return new HashSet<>(issueCategories);
    }
}
