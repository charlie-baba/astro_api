package com.sankore.astro.request.issuecategory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sankore.astro.pojo.Category;
import com.sankore.astro.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author Obi on 06/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IssueCategoryRequest extends BaseRequest {

    private Category category;

    private Set<String> issueTypes;

    public IssueCategoryRequest() {
        this.setContentClass(this.getClass().getSimpleName());
    }
}
