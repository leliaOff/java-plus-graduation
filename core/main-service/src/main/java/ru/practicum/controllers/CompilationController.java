package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.compilation.CompilationService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public Collection<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto find(@PathVariable Long compId) {
        return compilationService.find(compId);
    }
}
