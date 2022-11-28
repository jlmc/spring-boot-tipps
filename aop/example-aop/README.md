# Spring AOP

## What is Spring AOP?

Spring AOP enables Aspect-Oriented Programming in spring applications. In AOP, aspects enable the modularization of concerns such as transaction management, logging or security that cut across multiple types and objects (often termed crosscutting concerns).

AOP provides the way to dynamically add the cross-cutting concern before, after or around the actual logic using simple pluggable configurations. It makes easy to maintain code in the present and future as well. You can add/remove concerns without recompiling complete sourcecode simply by changing configuration files (if you are applying aspects suing XML configuration).

## What is advice, joinpoint or pointcut?

1. An important term in AOP is `advice`. It is the action taken by an `aspect` at a particular join-point.
2. `Joinpoint` is a point of execution of the program, such as the execution of a method or the handling of an exception. In Spring AOP, a joinpoint always represents a method execution.
3. `Pointcut` is a predicate or expression that matches join points.
4. `Advice` is associated with a pointcut expression and runs at any join point matched by the pointcut.
5. Spring uses the AspectJ pointcut expression language by default.


## Types of AOP Advices

There are five types of advice in spring AOP.

1. `Before advice`: Advice that executes before a join point, but which does not have the ability to prevent execution flow proceeding to the join point (unless it throws an exception).

2. `After returning advice`: Advice to be executed after a join point completes normally: for example, if a method returns without throwing an exception.

3. `After throwing advice`: Advice to be executed if a method exits by throwing an exception.

4. `After advice`: Advice to be executed regardless of the means by which a join point exits (normal or exceptional return).

5. `Around advice`: Advice that surrounds a join point such as a method invocation. This is the most powerful kind of advice. Around advice can perform custom behavior before and after the method invocation. It is also responsible for choosing whether to proceed to the join point or to shortcut the advised method execution by returning its own return value or throwing an exception.


### Example
