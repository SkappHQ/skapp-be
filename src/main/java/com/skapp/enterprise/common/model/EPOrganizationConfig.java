package com.skapp.enterprise.common.model;

import com.skapp.enterprise.common.type.EPOrganizationConfigType;
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
@Table(name = "ep_organization_config")
public class EPOrganizationConfig {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "config_title", updatable = false, unique = true, columnDefinition = "varchar(255)")
	private EPOrganizationConfigType epOrganizationConfigType;

	@Column(name = "config_value", nullable = false)
	private String epOrganizationConfigValue;

}
