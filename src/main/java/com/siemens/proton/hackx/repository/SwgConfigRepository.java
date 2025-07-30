package com.siemens.proton.hackx.repository;

import com.siemens.proton.hackx.model.SwitchgearDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwgConfigRepository extends JpaRepository<SwitchgearDTO, Integer> {
}
