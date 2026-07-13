package com.yomora.library.infrastructure.persistence;

import com.yomora.library.domain.ReadingStatus;
import com.yomora.library.domain.UserBook;
import com.yomora.library.domain.UserBookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaUserBookRepository implements UserBookRepository {
    private final SpringDataUserBookRepository repository;

    JpaUserBookRepository(SpringDataUserBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserBook save(UserBook userBook) {
        return repository.save(UserBookEntity.from(userBook)).toDomain();
    }

    @Override
    public boolean exists(UUID userId, UUID editionId) {
        return repository.existsByUserIdAndEditionId(userId, editionId);
    }

    @Override
    public Optional<UserBook> findOwned(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).map(UserBookEntity::toDomain);
    }

    @Override
    public List<UserBook> list(UUID userId, ReadingStatus status) {
        List<UserBookEntity> entities = status == null
                ? repository.findAllByUserIdOrderByCreatedAtDesc(userId)
                : repository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return entities.stream().map(UserBookEntity::toDomain).toList();
    }

    @Override
    public void delete(UserBook userBook) {
        repository.deleteById(userBook.id());
    }
}
