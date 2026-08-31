package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "views", source = "views")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "views", source = "views")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto);
}
