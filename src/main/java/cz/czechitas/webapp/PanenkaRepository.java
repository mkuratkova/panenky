package cz.czechitas.webapp;

import java.util.*;

public interface PanenkaRepository {

    List<Panenka> findAll();

    Panenka findById(Long id);

    Panenka save(Panenka zaznamKUlozeni);

    void delete(Long id);

    Panenka updatuj(Panenka zaznamKUlozeni);
}
