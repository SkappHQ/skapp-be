package com.skapp.community.peopleplanner.repository;

import com.skapp.community.peopleplanner.model.EmployeeVisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeVisaDao extends JpaRepository<EmployeeVisa, Long>, JpaSpecificationExecutor<EmployeeVisa> {

	Optional<EmployeeVisa> findByVisaId(Long item);

}
