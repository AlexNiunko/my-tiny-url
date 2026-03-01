package ru.emobile.mytinyurl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpireUrl {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name="id")
    private TinyUrl tinyUrl;

    private LocalDateTime expiredAt;

}
