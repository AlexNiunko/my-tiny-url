package ru.emobile.mytinyurl.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class TinyUrl {

    @Id
    private Long id;

    private String url;

    private String tiny;

    @OneToOne(mappedBy = "tinyUrl",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private ExpireUrl expire;

    public void setExpire(ExpireUrl expire) {
        this.expire = expire;
        if (expire != null) {
            expire.setTinyUrl(this);
        }
    }
}
