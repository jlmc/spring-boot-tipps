package io.github.jlmc.korders;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * This class allow to: Mockito + Spy: How to gather return values
 * First thing, you should be passing spy in as the constructor argument.
 * Since the factory is directly passed to the class and no getter for the created object is provided we need to intercept returning the object from the factory.
 *
 * <pre>
 *     RealFactory factory     = new RealFactory();
 *     RealFactory spy         = spy(factory);
 *     TestedClass testedClass = new TestedClass(spy);
 *
 *     // let's capture the return values from spy.create()
 *     ResultCaptor<RealThing> resultCaptor = new ResultCaptor<>();
 *     doAnswer(resultCaptor).when(spy).create();
 *
 *     // do something that will trigger a call to the factory
 *     testedClass.doSomething();
 *
 *     // validate the return object
 *     assertThat(resultCaptor.getResult()).isNotNull().isInstanceOf(RealThing.class);
 * </pre>
 */
public class ResultCaptor<T> implements Answer<T> {
    private T result = null;
    public T getResult() {
        return result;
    }

    @Override
    public T answer(InvocationOnMock invocationOnMock) throws Throwable {
        //noinspection unchecked
        this.result = (T) invocationOnMock.callRealMethod();
        return result;
    }
}
