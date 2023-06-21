package org.devsu.repository;

import org.devsu.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface AbstractEntityRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {

    Optional<T> findById(UUID id);

}
