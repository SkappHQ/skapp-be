package com.rootcode.skapp.common.repository;

import com.rootcode.skapp.common.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataResetDao extends JpaRepository<Organization, Long>, DataResetRepository {

}
