package io.costax.food4u.domain.services.internal;

import io.costax.food4u.domain.filters.DailySalesStatisticFilter;
import io.costax.food4u.domain.model.DailySalesStatistic;
import io.costax.food4u.domain.model.Request;
import io.costax.food4u.domain.services.DailySalesStatisticQueryService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailySalesStatisticQueryServiceImpl implements DailySalesStatisticQueryService {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<DailySalesStatistic> getStatistics(final DailySalesStatisticFilter filter, final String timeOffset) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<DailySalesStatistic> query = builder.createQuery(DailySalesStatistic.class);
        final Root<Request> root = query.from(Request.class);

        //final Expression<LocalDate> createdAt = root.get("createdAt").as(LocalDate.class);


        final Expression createdAtInTimeOffset = builder.function(
                "convert_tz",
                LocalDateTime.class,
                root.get("createdAt"),
                builder.literal("+00:00"),
                builder.literal(timeOffset));


        //convert_tz(created_at, '+00:00', '+02:00')
        var createdAtAsDate = builder.function(
                "date", LocalDateTime.class, createdAtInTimeOffset);



        query
                .select(
                        builder.construct(DailySalesStatistic.class,
                                createdAtAsDate.as(LocalDate.class),
                                builder.count(root.get("id")),
                                builder.sum(root.get("totalValue"))
                        ))
                .groupBy(createdAtAsDate.as(LocalDate.class));

        return em.createQuery(query).getResultList();
    }
}