package br.com.petro.eventos.repository;

import br.com.petro.eventos.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event e SET e.deleted = true, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void softDeleteById(@Param("id") Long id);
}