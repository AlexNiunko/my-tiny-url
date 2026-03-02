package ru.emobile.mytinyurl.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.emobile.mytinyurl.entity.TinyUrl;

public interface TinyUrlRepository extends JpaRepository<TinyUrl, Long> {

    @Query(
            """ 
            SELECT t from TinyUrl t where t.tiny=:alias
            """
    )
    Optional<TinyUrl> findByAlias(String alias);

    @Query(
            """
            SELECT t FROM TinyUrl t left outer join fetch t.expire where t.tiny=:tinyUrl
            """
    )
    Optional<TinyUrl> getUrl(String tinyUrl);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM tiny_url AS t USING expire_url AS e WHERE t.id=e.id AND e.expired_at<:time
            """,nativeQuery = true
    )
    void delete(LocalDateTime time);

}
