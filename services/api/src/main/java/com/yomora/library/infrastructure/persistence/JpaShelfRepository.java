package com.yomora.library.infrastructure.persistence;

import com.yomora.library.domain.Shelf;
import com.yomora.library.domain.ShelfRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaShelfRepository implements ShelfRepository {
    private final SpringDataShelfRepository repository;

    JpaShelfRepository(SpringDataShelfRepository repository) {
        this.repository = repository;
    }

    @Override
    public Shelf save(Shelf shelf) {
        return repository.save(ShelfEntity.from(shelf)).toDomain();
    }

    @Override
    public Optional<Shelf> findOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(ShelfEntity::toDomain);
    }

    @Override
    public List<Shelf> list(UUID userId) {
        return repository.findAllByUserIdOrderByName(userId).stream().map(ShelfEntity::toDomain).toList();
    }

    @Override
    public void delete(Shelf shelf) {
        repository.deleteById(shelf.id());
    }
}
