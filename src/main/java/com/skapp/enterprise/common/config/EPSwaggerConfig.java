package com.skapp.enterprise.common.config;

import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EPSwaggerConfig {

	@Bean
	public OpenApiCustomizer addTenantIdHeaderCustomizer() {
		List<String> excludedPaths = List.of("/v1/ep/tenant/create");

		return openApi -> openApi.getPaths()
			.forEach((path, pathItem) -> pathItem.readOperations().forEach(operation -> {
				if (!excludedPaths.contains(path)) {
					operation.addParametersItem(new HeaderParameter().name("X-Tenant-ID")
						.description("Tenant ID for multi-tenancy")
						.schema(new StringSchema())
						.required(true));
				}
			}));
	}

}
