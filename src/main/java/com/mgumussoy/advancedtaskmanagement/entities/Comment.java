package com.mgumussoy.advancedtaskmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "comments")
public class Comment extends BaseEntity {
    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity taskEntity;
}
