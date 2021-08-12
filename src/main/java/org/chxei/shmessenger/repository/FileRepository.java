package org.chxei.shmessenger.repository;

import org.chxei.shmessenger.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}