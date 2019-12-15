package io.costax.food4u.domain.repository.internal;

import io.costax.food4u.domain.repository.CustomJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

@SuppressWarnings("SpringJavaConstructorAutowiringInspection")
public class CustomJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
        implements CustomJpaRepository<T, ID> {

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
    public T refresh(final T obj) {
        // final String entityName = getDomainClass().getName();
        //entityManager.flush();
        manager.refresh(obj);
        return obj;
    }
}