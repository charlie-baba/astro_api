package com.sankore.astro.enums;

import lombok.Getter;

/**
 * @author Obi on 09/05/2019
 */
@Getter
public enum ResponseCode {

    Success(200, "Successful"),
    Info(205, "Successful"),
    Empty_Request(204, "Your Request Body is Empty"),
    Internal_Server_Error(500, "Something went wrong. Please try again later."),
    Bad_Request(400, "Bad/Invalid Request"),
    Not_Found(404, "Requested Resource Not Found"),
    Invalid_Request_Target(401, "Unable to resolve the request target"),
    Service_Connection_Timeout(401, "The integration service connection timed out"),
    Unauthorized(403, "You do not have the permission to perform this operation")
    ;

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
