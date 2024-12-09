package com.skapp.community.common.model;

import com.skapp.community.common.type.OrganizationConfigType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organization_config")
public class OrganizationConfig {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "config_title", updatable = false, unique = true, columnDefinition = "varchar(255)")
	private OrganizationConfigType organizationConfigType;

	@Column(name = "config_value", nullable = false)
	private String organizationConfigValue;

}
