package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.model.Hit;
import ru.practicum.server.model.Stat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT new ru.practicum.server.model.Stat(hit.app, hit.uri, count(hit.id))" +
            "FROM Hit as hit " +
            "WHERE hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<Stat> getStat(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.server.model.Stat(hit.app, hit.uri, count(hit.id))" +
            "FROM Hit as hit " +
            "WHERE hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "AND hit.uri IN :uris " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<Stat> getStat(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") Collection<String> uris);

    @Query("SELECT new ru.practicum.server.model.Stat(hit.app, hit.uri, count(DISTINCT hit.ip))" +
            "FROM Hit as hit " +
            "WHERE hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<Stat> getUniqueStat(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.server.model.Stat(hit.app, hit.uri, count(DISTINCT hit.ip))" +
            "FROM Hit as hit " +
            "WHERE hit.timestamp >= :start " +
            "AND hit.timestamp <= :end " +
            "AND hit.uri IN :uris " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<Stat> getUniqueStat(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") Collection<String> uris);
}
