package com.viewstar.monitorfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  ActionLogDao extends JpaRepository<ActionLog, Long> {
}
