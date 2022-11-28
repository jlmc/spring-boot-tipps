package io.costax.food4u.domain.repository.internal;

import io.costax.food4u.domain.repository.CustomJpaRepository;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("SpringJavaConstructorAutowiringInspection")
public class CustomJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
        implements CustomJpaRepository<T, ID> {

    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    private EntityManager manager;

    public CustomJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                   EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.manager = entityManager;
    }

    public CustomJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation,
                                   final EntityManager entityManager,
                                   final EntityManager manager) {
        super(entityInformation, entityManager);
        this.manager = manager;
    }

    @Override
    public Optional<T> findByIdForceIncrement(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        final Class<T> domainClass = super.getDomainClass();

        return Optional.ofNullable(manager.find(domainClass, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT));
    }

    @Override
    public Optional<T> findBySimpleNaturalId(final Serializable naturalId) {
        return manager
                .unwrap(Session.class)
                .bySimpleNaturalId(this.getDomainClass())
                .loadOptional(naturalId);
    }

    @Override
    public Optional<T> findByNaturalId(final Map<String, Object> naturalIds) {
        NaturalIdLoadAccess<T> loadAccess = manager
                .unwrap(Session.class)
                .byNaturalId(this.getDomainClass());

        naturalIds.forEach(loadAccess::using);

        return loadAccess.loadOptional();
    }

    @Override
    public T refresh(final T obj) {
        // final String entityName = getDomainClass().getName();
        //entityManager.flush();
        manager.refresh(obj);
        return obj;
    }

    @Override
    public void detach(final T obj) {
        manager.detach(obj);
    }


    @Override
    public void clearAllContext() {
        manager.clear();
    }
}