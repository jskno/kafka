package com.jskno.email.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "processed-events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProcessEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String productId;

}
