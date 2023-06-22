package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public void deleteDirector(int id) {
        directorStorage.delete(id);
    }
}
