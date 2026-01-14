package com.task.fileuploadapp.repository;

import com.task.fileuploadapp.domain.FixedExtensionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedExtensionPolicyRepository extends JpaRepository<FixedExtensionPolicy, String> {

    List<FixedExtensionPolicy> findAllByOrderByExtensionAsc();
}
