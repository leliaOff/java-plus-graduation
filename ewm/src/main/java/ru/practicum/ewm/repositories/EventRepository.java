package ru.practicum.ewm.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    boolean existsByCategoryId(Long categoryId);

    List<Event> findAllByInitiatorId(Long userId, PageRequest request);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    @Query("SELECT event FROM Event event WHERE event.id IN (:ids)")
    List<Event> findByIdIn(@Param("ids") Collection<Long> ids);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories)  AND" +
            "(CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) AND"
            + "(CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findAllByFilters(@Param("users") List<Long> users,
                                 @Param("states") List<String> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);


    interface EventSpecification {
        static Specification<Event> byText(String text) {
            return ((root, query, criteriaBuilder) -> {
                if (text == null || text.isBlank()) {
                    return null;
                }
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("description"), "%" + text + "%"),
                        criteriaBuilder.like(root.get("annotation"), "%" + text + "%")
                );
            });
        }

        static Specification<Event> byCategories(List<Category> categories) {
            return ((root, query, criteriaBuilder) -> {
                if (categories == null || categories.isEmpty()) {
                    return null;
                }
                return root.get("category").in(categories);
            });
        }

        static Specification<Event> byPaid(Boolean paid) {
            return ((root, query, criteriaBuilder) -> {
                if (paid == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("paid"), paid);
            });
        }

        static Specification<Event> byRangeStart(LocalDateTime rangeStart) {
            return ((root, query, criteriaBuilder) -> {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now));
            });
        }

        static Specification<Event> byRangeEnd(LocalDateTime rangeEnd) {
            return ((root, query, criteriaBuilder) -> {
                if (rangeEnd == null) {
                    return null;
                }
                return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
            });
        }

        static Specification<Event> byOnlyAvailable(Boolean onlyAvailable) {
            return ((root, query, criteriaBuilder) -> {
                if (onlyAvailable == null) {
                    return null;
                }
                return criteriaBuilder.or(
                        criteriaBuilder.greaterThan(root.get("participantLimit"), root.get("confirmedRequests")),
                        criteriaBuilder.equal(root.get("participantLimit"), 0)
                );
            });
        }

        static Specification<Event> byState(EventState state) {
            return ((root, query, criteriaBuilder) -> {
                if (state == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("state"), state);
            });
        }
    }
}
