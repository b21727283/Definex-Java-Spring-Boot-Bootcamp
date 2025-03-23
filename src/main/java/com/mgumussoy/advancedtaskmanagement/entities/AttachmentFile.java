package com.mgumussoy.advancedtaskmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "attachment_files")
public class AttachmentFile extends BaseEntity {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    private String description;

    @Lob
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity taskEntity;
}
