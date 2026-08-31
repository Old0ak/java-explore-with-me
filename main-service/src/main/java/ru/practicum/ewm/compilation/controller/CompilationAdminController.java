package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Администратор: запрос на создание подборки с заголовком: '{}'", newCompilationDto.getTitle());
        CompilationDto created = compilationService.createCompilation(newCompilationDto);
        log.info("Администратор: подборка успешно создана. id={}, title='{}'", created.getId(), created.getTitle());
        return created;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Администратор: запрос на удаление подборки id={}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Администратор: подборка id={} успешно удалена", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateRequest) {
        log.info("Администратор: запрос на обновление подборки id={}", compId);
        CompilationDto updated = compilationService.updateCompilation(compId, updateRequest);
        log.info("Администратор: подборка id={} успешно обновлена. Текущий заголовок: '{}'",
                compId, updated.getTitle());
        return updated;
    }
}
