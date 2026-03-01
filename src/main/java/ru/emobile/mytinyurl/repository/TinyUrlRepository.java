package ru.emobile.mytinyurl.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     
     
     """
    )
    Optional<TinyUrl> getUrl(String tinyUrl);

}
