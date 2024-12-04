package com.rootcode.skapp.timeplanner.repository;

import com.rootcode.skapp.timeplanner.model.TimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRequestDao extends JpaRepository<TimeRequest, Long>, TimeRequestRepository {

}
