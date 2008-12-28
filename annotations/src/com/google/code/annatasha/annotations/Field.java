package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Field {
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface ReadPermissions {
		Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface WritePermissions {
		Class<?>[] value();
	}

}
