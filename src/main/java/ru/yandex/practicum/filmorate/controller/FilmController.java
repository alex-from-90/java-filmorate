package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.endpoints.ApiEndpointsFilm;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(ApiEndpointsFilm.FILMS)
@Slf4j
@AllArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping(ApiEndpointsFilm.GET_FILMS)
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @PostMapping(ApiEndpointsFilm.CREATE_FILM)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинту: '{}' на добавление фильма с ID={}", ApiEndpointsFilm.CREATE_FILM, film.getId());
        return filmStorage.create(film);
    }

    @PutMapping(ApiEndpointsFilm.UPDATE_FILM)
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к эндпоинту: '{}' на обновление фильма с ID={}", ApiEndpointsFilm.UPDATE_FILM, film.getId());
        return filmStorage.update(film);
    }

    @GetMapping(ApiEndpointsFilm.GET_FILM_BY_ID)
    public Film getFilmById(@PathVariable Long id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping(ApiEndpointsFilm.GET_POPULAR)
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        return filmService.getPopular(count);
    }

    @PutMapping(ApiEndpointsFilm.ADD_LIKE)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(ApiEndpointsFilm.DELETE_LIKE)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping(ApiEndpointsFilm.DELETE_FILM)
    public Film delete(@PathVariable Long id) {
        log.info("Получен DELETE-запрос к эндпоинту: '{}' на удаление фильма с ID={}", ApiEndpointsFilm.DELETE_FILM, id);
        return filmStorage.delete(id);
    }
}