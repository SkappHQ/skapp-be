package com.rootcode.skapp.peopleplanner.repository;

import com.rootcode.skapp.common.type.ModuleType;
import com.rootcode.skapp.peopleplanner.model.ModuleRoleRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRoleRestrictionDao extends JpaRepository<ModuleRoleRestriction, ModuleType> {

}
