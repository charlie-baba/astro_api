package com.sankore.astro.response.issuecategory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Category;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Obi on 06/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueCategoryResponse extends BaseResponse {

    private Category category;

    public IssueCategoryResponse() { }

    public IssueCategoryResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
