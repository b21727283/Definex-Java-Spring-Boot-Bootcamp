package com.mgumussoy.advancedtaskmanagement.entities;

import com.mgumussoy.advancedtaskmanagement.enums.TaskPriority;
import com.mgumussoy.advancedtaskmanagement.enums.TaskState;
import com.mgumussoy.advancedtaskmanagement.listeners.PreventTaskStateChangeListener;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false)
@EntityListeners(PreventTaskStateChangeListener.class)
@Table(name = "tasks")
public class TaskEntity extends BaseEntity {
    @Lob
    @Column(nullable = false)
    private String userStory;

    @Lob
    @Column(nullable = false)
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AttachmentFile> attachments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserEntity assignee;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String reasonForStateChange;
}
