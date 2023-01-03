package io.github.jlmc.korders.processor.domain.model.aggregates;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.EmbeddableInstantiator;
import org.hibernate.metamodel.spi.ValueAccess;

/**
 * this an example of hibernate EmbeddableInstantiator
 *
 * @see <a href="https://docs.jboss.org/hibernate/orm/6.1/userguide/html_single/Hibernate_User_Guide.html#embeddables">hibernate 6 Embeddable Instantiator</a>
 *
 * @see org.hibernate.metamodel.spi.EmbeddableInstantiator
 *
 * <pre>
 *     Example 1: In the fields
 *
 *     @Embeded
 *     @EmbeddableInstantiator( FailureRecordCauseEmbeddableInstantiator.class )
 *     public Name name;
 *
 *     @ElementCollection
 * 	   @Embeded
 * 	   @EmbeddableInstantiator( FailureRecordCauseEmbeddableInstantiator.class )
 * 	   public Set<Name> aliases;
 *
 *     Example 2: In the class
 *
 *     @Embeddable
 *     @EmbeddableInstantiator( NameInstantiator.class )
 *     public class Name {
 *
 *    Example 3: Lastly, @org.hibernate.annotations.EmbeddableInstantiatorRegistration may be used, which is useful when the application developer does not control the embeddable to be able to apply the instantiator on the embeddable.
 *
 *    @EmbeddableInstantiatorRegistration( embeddableClass = Name.class, instantiator = NameInstantiator.class )
 *    public class Person {
 * </pre>
 */
public class FailureRecordCauseEmbeddableInstantiator implements EmbeddableInstantiator {
    @Override
    public Object instantiate(ValueAccess valuesAccess, SessionFactoryImplementor sessionFactoryImplementor) {
        final Object[] values = valuesAccess.getValues();
        // valuesAccess contains attribute values in alphabetical order
        final String className = (String) values[0];
        final String message = (String) values[1];
        return new FailureRecordCause( className, message );
    }

    @Override
    public boolean isInstance(Object o, SessionFactoryImplementor sessionFactoryImplementor) {
        return o instanceof FailureRecordCause;
    }

    @Override
    public boolean isSameClass(Object o, SessionFactoryImplementor sessionFactoryImplementor) {
        return o.getClass().equals( FailureRecordCause.class );
    }
}
