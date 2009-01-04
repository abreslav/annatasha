package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Constraint {

	/**
	 * Constrains parameter. To pass the validation it must have been marked.
	 * <li> When applied to method, the constraint is put on <code>this</code>
	 * value. Application to static method is error.
	 * <li> When applied to the local variable or private field, it's verified
	 * to be either null or assigned to from marked value. Application to
	 * non-private field is error.
	 * 
	 * @author ivan
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD,
			ElementType.LOCAL_VARIABLE })
	@Inherited
	public @interface Marked {
		Class<?> value();
	}

}
