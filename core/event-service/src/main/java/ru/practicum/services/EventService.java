package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.models.Category;
import ru.practicum.models.Event;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.repositories.EventRepository.EventSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final StatEventService statEventService;
    private final UserEventService userEventService;

    @Transactional
    public EventDto addEvent(Long userId, NewEventDto newEventDto) {
        UserDto userDto = userEventService.getUser(userId);
        Event event = EventMapper.toModel(newEventDto, userDto.id());
        Event savedEvent = eventRepository.save(event);
        log.info("Added event: {}", savedEvent);
        return EventMapper.toDto(savedEvent, userDto, 0L);
    }

    public List<EventShortDto> getPrivateEvents(Long userId, PageRequest request) {
        UserDto userDto = userEventService.getUser(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, request);
        return events.stream().map(event -> EventMapper.toShortDto(event, userDto, statEventService.getViews(event))).toList();
    }

    public EventDto getPrivateEvent(Long userId, Long eventId) {
        UserDto userDto = userEventService.getUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toDto(event, userDto, statEventService.getViews(event));
    }

    @Transactional
    public EventDto updatePrivateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventRequest) {
        UserDto userDto = userEventService.getUser(userId);
        Event oldEvent = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!oldEvent.getState().equals(EventState.CANCELED) && !oldEvent.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Event with id=" + eventId + " cannot be updated");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateEventRequest);
        return EventMapper.toDto(eventRepository.save(updatedEvent), userDto, statEventService.getViews(oldEvent));
    }

    @Transactional
    public void setConfirmed(Long eventId, Integer confirmedRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        event.setConfirmedRequests(confirmedRequest);
        eventRepository.save(event);
    }

    public EventDto find(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        UserDto userDto = userEventService.getUser(event.getInitiatorId());
        return EventMapper.toDto(event, userDto, statEventService.getViews(event));
    }

    public EventDto findPublished(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event with id=" + eventId + " was not found"));
        UserDto userDto = userEventService.getUser(event.getInitiatorId());
        return EventMapper.toDto(event, userDto, statEventService.getViews(event));
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
        Map<Long, UserDto> users = userEventService.getUsersByEvents(events);

        Collection<EventShortDto> eventShortDtoCollection = events.stream()
                .map(event -> EventMapper.toShortDto(event, users.get(event.getInitiatorId()), views.get(event.getId())))
                .toList();
        if (sort.equals(EventSort.VIEWS)) {
            return eventShortDtoCollection.stream().sorted(Comparator.comparing(EventShortDto::getViews)).toList();
        }
        return eventShortDtoCollection;
    }


    public List<EventDto> getAdminEvents(EventAdminFilterDto filter) {
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

        Map<Long, UserDto> users = userEventService.getUsersByEvents(events);

        return events.stream()
                .map(event -> EventMapper.toDtoWithoutViews(event, users.get(event.getInitiatorId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateAdminRequest) {
        Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is PUBLISHED");
        }
        if (oldEvent.getState().equals(EventState.CANCELED)) {
            throw new InvalidDataException("Event with id=" + eventId + " is CANCELED");
        }
        Event updatedEvent = EventMapper.mergeModel(oldEvent, updateAdminRequest);
        UserDto userDto = userEventService.getUser(updatedEvent.getInitiatorId());
        return EventMapper.toDto(eventRepository.save(updatedEvent), userDto, 0L);
    }

}
