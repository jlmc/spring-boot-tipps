package io.costax.food4u.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * The annotation {@link NoRepositoryBean} is used to specify that the annotated interface type
 * should not be taken into account when instantiating a new repository.
 * The spring JPA should not instantiate an implementation for this interface.
 */
@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID> {

    T refresh(T obj);

    void detach(final T obj);

    void clearAllContext();

    Optional<T> findByIdForceIncrement(ID id);

    /**
     * use this method when your entity has a single field annotated with @NaturalId
     */
    Optional<T> findBySimpleNaturalId(Serializable naturalId);

    /**
     * use this method when your entity has more than one field annotated with @NaturalId
     */
    Optional<T> findByNaturalId(Map<String, Object> naturalIds);
}
