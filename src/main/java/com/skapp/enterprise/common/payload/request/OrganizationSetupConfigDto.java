package com.skapp.enterprise.common.payload.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.skapp.enterprise.common.payload.CreateTenantDto;
import com.skapp.enterprise.common.type.LoginMethod;
import com.skapp.enterprise.common.util.deserializer.LoginMethodDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrganizationSetupConfigDto {

	private String companyName;

	private String companyEmail;

	private String companyWebsite;

	private String accountUrl;

	private String country;

	@JsonDeserialize(using = LoginMethodDeserializer.class)
	private LoginMethod loginMethod;

	private CreateTenantDto createTenantDto;

	private LocalDate creationDate;

}
