package kr.co.seoulit.logistics.sys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dataset {
	public String name();
	
	/*
	 - class : @Target(ElementType.TYPE)
 	- field : @Target(ElementType.FIELD)
	  */
}
