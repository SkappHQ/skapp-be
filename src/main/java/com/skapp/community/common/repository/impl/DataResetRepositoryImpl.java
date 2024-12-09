package com.skapp.community.common.repository.impl;

import com.skapp.community.common.repository.DataResetRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RequiredArgsConstructor
public class DataResetRepositoryImpl implements DataResetRepository {

	@NonNull
	private EntityManager entityManager;

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;

	@Override
	@Transactional
	public void resetDatabase() {
		String databaseSchema = extractDatabaseName(dataSourceUrl);

		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

		List<?> tableResults = entityManager
			.createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '"
					+ databaseSchema + "' AND table_type = 'BASE TABLE' "
					+ "AND LOWER(table_name) NOT IN ('databasechangelog', 'databasechangeloglock')")
			.getResultList();

		List<String> tables = tableResults.stream().map(Object::toString).toList();

		for (String table : tables) {
			entityManager.createNativeQuery("TRUNCATE TABLE `" + table + "`").executeUpdate();
		}

		entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
	}

	private String extractDatabaseName(String url) {
		if (url != null && url.contains("/")) {
			String withoutParams = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
			return withoutParams.substring(withoutParams.lastIndexOf("/") + 1);
		}
		return null;
	}

}
