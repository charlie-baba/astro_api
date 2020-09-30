package com.sankore.astro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveUserStore implements Serializable {

	public List<String> users;

	public ActiveUserStore() {
		users = new ArrayList<>();
	}

}