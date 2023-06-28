package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.database.impl.DirectorDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public Collection<Director> getDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorDbStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public Director createDirector(Director director) {
        return directorDbStorage.createDirector(director);
    }

    public void deleteDirector(int id) {
        directorDbStorage.delete(id);
    }
}
