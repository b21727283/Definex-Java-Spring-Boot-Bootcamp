package com.mgumussoy.advancedtaskmanagement.repositories;

import com.mgumussoy.advancedtaskmanagement.entities.AttachmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, Long> {
}
