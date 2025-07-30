package com.siemens.proton.hackx.repository;

import com.siemens.proton.hackx.model.LocationConfigModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GridConfigRepository extends JpaRepository<LocationConfigModel, Integer> {
}
