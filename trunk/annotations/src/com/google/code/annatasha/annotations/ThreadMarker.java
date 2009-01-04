package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the types which may be suited as thread marker
 * 
 * @author Ivan Egorov
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface ThreadMarker {

}
