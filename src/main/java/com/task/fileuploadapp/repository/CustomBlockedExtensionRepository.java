package com.task.fileuploadapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.task.fileuploadapp.domain.CustomBlockedExtension;

import jakarta.persistence.LockModeType;
import java.util.List;

public interface CustomBlockedExtensionRepository extends JpaRepository<CustomBlockedExtension, Long> {

    boolean existsByExtension(String extension);

    List<CustomBlockedExtension> findAllByOrderByCreatedAtDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CustomBlockedExtension c")
    List<CustomBlockedExtension> findAllForUpdate();
}
