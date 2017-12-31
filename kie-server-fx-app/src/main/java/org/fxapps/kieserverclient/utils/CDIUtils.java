package org.fxapps.kieserverclient.utils;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.util.AnnotationLiteral;

public class CDIUtils {

	private static SeContainer container;

	static {
		SeContainerInitializer initializer = SeContainerInitializer.newInstance();
		container = initializer.initialize();
	}

	private CDIUtils() {

	}

	public static void fireEvent(Object event, AnnotationLiteral<?> annotationLiteral) {
		container.getBeanManager().fireEvent(event, annotationLiteral);
	}

	public static <T> T getBean(Class<T> type, Annotation... qualifiers) {
		return (T) container.select(type, qualifiers).get();
	}
}
