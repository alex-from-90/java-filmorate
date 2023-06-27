package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getDirectors() {
        log.info("Получен GET-запрос к эндпоинту: '/directors' на получение всех режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable @Valid int id) {
        log.info("Получен GET-запрос к эндпоинту: '/directors' на получение режиссера с ID={}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен POST-запрос к эндпоинту: '/directors' на добавление режиссера с ID={}", director.getId());
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен PUT-запрос к эндпоинту: '/directors' на обновление режиссера с ID={}", director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/directors' на удаление режиссера с ID={}", id);
        directorService.deleteDirector(id);
    }
}