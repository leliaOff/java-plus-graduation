package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.ConflictException;
import ru.practicum.ewm.exceptions.InvalidDataException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.mappers.UserMapper;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.repositories.EventRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.repositories.EventRepository.EventSpecification.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final StatEventService statEventService;

    public EventService(CategoryRepository categoryRepository,
                        EventRepository eventRepository,
                        UserService userService,
                        StatEventService statEventService
    ) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.statEventService = statEventService;
    }

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        UserDto userDto = userService.getUser(userId);
        Event event = eventRepository.save(EventMapper.toModel(newEventDto, UserMapper.toModel(userDto)));
        log.info("Added event: {}", event);
        return EventMapper.toDto(event, 0L);
    }

    public List<EventShortDto> getPrivateEvents(Long userId, PageRequest request) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, request);
        return events.stream().map(event -> EventMapper.toShortDto(event, statEventService.getViews(event))).toList();
    }

    public EventFullDto getPrivateEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toDto(event, statEventService.getViews(event));
    }

    public EventFullDto updatePrivateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest) {
        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!oldEvent.getState().equals(EventState.CANCELED) && !oldEvent.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Event with id=" + eventId + " cannot be updated");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateEventRequest);
        return EventMapper.toDto(eventRepository.save(updatedEvent), statEventService.getViews(oldEvent));
    }

    public EventFullDto find(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event with id=" + eventId + " was not found"));
        return EventMapper.toDto(event, statEventService.getViews(event));
    }

    public Collection<EventShortDto> getEvents(EventFilterDto filter, EventSort sort, Integer from, Integer size) {
        List<Category> categories = categoryRepository.findByIdIn(filter.getCategories());
        if (filter.getCategories() != null && filter.getCategories().size() != categories.size()) {
            throw new BadRequestException("One or more categories not found");
        }
        Specification<Event> specification = byText(filter.getText())
                .and(byCategories(categories))
                .and(byPaid(filter.getPaid()))
                .and(byRangeStart(filter.getRangeStart()))
                .and(byRangeEnd(filter.getRangeEnd()))
                .and(byOnlyAvailable(filter.getOnlyAvailable()))
                .and(byState(EventState.PUBLISHED));
        Collection<Event> events = eventRepository.findAll(specification,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"))
        ).toList();
        HashMap<Long, Long> views = statEventService.getViews(events);
        Collection<EventShortDto> eventShortDtoCollection = events.stream()
                .map(event -> EventMapper.toShortDto(event, views.get(event.getId())))
                .toList();
        if (sort.equals(EventSort.VIEWS)) {
            return eventShortDtoCollection.stream().sorted(Comparator.comparing(EventShortDto::getViews)).toList();
        }
        return eventShortDtoCollection;
    }


    public List<EventFullDto> getAdminEvents(EventAdminFilterDto filter) {
        log.info("Executing getAdminEvents with filter: {}", filter);

        Pageable pageable = PageRequest.of(filter.getFrom(), filter.getSize());

        List<Event> events = eventRepository.findAllByFilters(
                filter.getUsers(),
                filter.getStates(),
                filter.getCategories(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                pageable
        );
        log.info("Events found: {}", events.size());


        return events.stream()
                .map(EventMapper::toDtoWithoutViews)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateAdminRequest) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is PUBLISHED");
        }
        if (oldEvent.getState().equals(EventState.CANCELED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is CANCELED");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateAdminRequest);

        return EventMapper.toDto(eventRepository.save(updatedEvent), 0L);
    }

}
