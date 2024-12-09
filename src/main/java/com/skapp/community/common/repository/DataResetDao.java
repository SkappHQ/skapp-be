package com.skapp.community.common.repository;

import com.skapp.community.common.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataResetDao extends JpaRepository<Organization, Long>, DataResetRepository {

}
