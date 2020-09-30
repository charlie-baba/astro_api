package com.sankore.server.emailreader.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Obi on 28/05/2019
 */
@Getter
@Setter
@ToString
public class EmailExtract implements Serializable {

    private List<String> email = new ArrayList<>();

    private List<String> fullName = new ArrayList<>();

    private String subject;

    private Date date;

    private String body;

    private String clientCode;
}
