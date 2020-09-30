package com.sankore.astro.response.sla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.pojo.Category;
import com.sankore.astro.pojo.IssueTypePojo;
import com.sankore.astro.pojo.Sla;
import com.sankore.astro.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Obi on 07/06/2019
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlaResponse extends BaseResponse {

    private Sla sla;

    private List<IssueTypePojo> issueTypes;

    private List<Category> categories;

    private List<String> timeUnits;

    public SlaResponse() {}

    public SlaResponse(int code, String message) {
        this.setResponseCode(code);
        this.setResponseMessage(message);
    }

    public SlaResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getMessage());
    }
}
