package com.rootcode.skapp.peopleplanner.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleResponseDto {

	private String module;

	private List<String> roles;

}
