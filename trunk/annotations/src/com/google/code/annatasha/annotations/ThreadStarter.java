/**
 * 
 */
package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark the parameter of method with {@link ThreadStarter} when you want to
 * prevent the method from calling {@link Runnable#run()} method of the
 * parameter in this thread.
 * 
 * @author Ivan Egorov
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadStarter {

}
