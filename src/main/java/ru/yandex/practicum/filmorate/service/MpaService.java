package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.impl.MpaDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return mpaDbStorage.getMpaById(id);
    }
}