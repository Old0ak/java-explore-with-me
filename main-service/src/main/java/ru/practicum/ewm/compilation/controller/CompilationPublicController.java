package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Публичный API: запрос списка подборок. pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilations = compilationService.getCompilations(pinned, from, size);
        log.info("Публичный API: успешно возвращено {} подборок", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Публичный API: запрос подборки с id={}", compId);
        CompilationDto compilation = compilationService.getCompilationById(compId);
        log.info("Публичный API: успешно найдена подборка id={}, title='{}'", compId, compilation.getTitle());
        return compilation;
    }
}
