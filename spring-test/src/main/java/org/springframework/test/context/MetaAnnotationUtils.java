/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context;

import java.lang.annotation.Annotation;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import static org.springframework.core.annotation.AnnotationUtils.*;

/**
 * TODO Document MetaAnnotationUtils.
 * 
 * @author Sam Brannen
 * @since 4.0
 */
abstract class MetaAnnotationUtils {

	private MetaAnnotationUtils() {
		/* no-op */
	}

	/**
	 * TODO Document findAnnotationDescriptor().
	 *
	 * @param clazz the class to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation found, or {@code null} if none found
	 */
	public static <T extends Annotation> AnnotationDescriptor<T> findAnnotationDescriptor(Class<?> clazz,
			Class<T> annotationType) {

		Assert.notNull(annotationType, "Annotation type must not be null");

		if (clazz == null || clazz.equals(Object.class)) {
			return null;
		}

		// Declared locally?
		if (isAnnotationDeclaredLocally(annotationType, clazz)) {
			return new AnnotationDescriptor<T>(clazz, clazz.getAnnotation(annotationType));
		}

		// Declared on a stereotype annotation (i.e., as a meta-annotation)?
		if (!Annotation.class.isAssignableFrom(clazz)) {
			for (Annotation stereotype : clazz.getAnnotations()) {
				T annotation = stereotype.annotationType().getAnnotation(annotationType);
				if (annotation != null) {
					return new AnnotationDescriptor<T>(clazz, stereotype, annotation);
				}
			}
		}

		// Declared on an interface?
		for (Class<?> ifc : clazz.getInterfaces()) {
			AnnotationDescriptor<T> descriptor = findAnnotationDescriptor(ifc, annotationType);
			if (descriptor != null) {
				return descriptor;
			}
		}

		// Declared on a superclass?
		return findAnnotationDescriptor(clazz.getSuperclass(), annotationType);
	}


	/**
	 * Descriptor for an {@link Annotation}, including the {@linkplain
	 * #getDeclaringClass() class} on which the annotation is <em>declared</em>
	 * as well as the actual {@linkplain #getAnnotation() annotation} instance.
	 *
	 * <p>
	 * If the annotation is used as a meta-annotation, the descriptor also includes
	 * the {@linkplain #getStereotype() stereotype} on which the annotation is
	 * present. In such cases, the <em>declaring class</em> is not directly
	 * annotated with the annotation but rather indirectly via the stereotype.
	 *
	 * <p>
	 * Given the following example, if we are searching for the {@code @Transactional}
	 * annotation <em>on</em> the {@code TransactionalTests} class, then the
	 * properties of the {@code AnnotationDescriptor} would be as follows.
	 *
	 * <ul>
	 * <li>declaringClass: {@code TransactionalTests} class object</li>
	 * <li>stereotype: {@code null}</li>
	 * <li>annotation: instance of the {@code Transactional} annotation</li>
	 * </ul>
	 *
	 * <pre style="code">
	 * &#064;Transactional
	 * &#064;ContextConfiguration({"/test-datasource.xml", "/repository-config.xml"})
	 * public class TransactionalTests { }
	 * </pre>
	 *
	 * <p>
	 * Given the following example, if we are searching for the {@code @Transactional}
	 * annotation <em>on</em> the {@code UserRepositoryTests} class, then the
	 * properties of the {@code AnnotationDescriptor} would be as follows.
	 * 
	 * <ul>
	 * <li>declaringClass: {@code UserRepositoryTests} class object</li>
	 * <li>stereotype: instance of the {@code RepositoryTests} annotation</li>
	 * <li>annotation: instance of the {@code Transactional} annotation</li>
	 * </ul>
	 *
	 * <pre style="code">
	 * &#064;Transactional
	 * &#064;ContextConfiguration({"/test-datasource.xml", "/repository-config.xml"})
	 * &#064;Retention(RetentionPolicy.RUNTIME)
	 * public &#064;interface RepositoryTests { }
	 *
	 * &#064;RepositoryTests
	 * public class UserRepositoryTests { }
	 * </pre>
	 *
	 * @author Sam Brannen
	 * @since 4.0
	 */
	public static class AnnotationDescriptor<T extends Annotation> {

		private final Class<?> declaringClass;
		private final Annotation stereotype;
		private final T annotation;


		public AnnotationDescriptor(Class<?> declaringClass, T annotation) {
			this(declaringClass, null, annotation);
		}

		public AnnotationDescriptor(Class<?> declaringClass, Annotation stereotype, T annotation) {
			Assert.notNull(declaringClass, "declaringClass must not be null");
			Assert.notNull(annotation, "annotation must not be null");

			this.declaringClass = declaringClass;
			this.stereotype = stereotype;
			this.annotation = annotation;
		}

		public Class<?> getDeclaringClass() {
			return this.declaringClass;
		}

		public T getAnnotation() {
			return this.annotation;
		}

		public Class<? extends Annotation> getAnnotationType() {
			return this.annotation.annotationType();
		}

		public Annotation getStereotype() {
			return this.stereotype;
		}

		public Class<? extends Annotation> getStereotypeType() {
			return this.stereotype == null ? null : this.stereotype.annotationType();
		}

		/**
		 * Provide a textual representation of this {@code AnnotationDescriptor}.
		 */
		@Override
		public String toString() {
			return new ToStringCreator(this)//
			.append("declaringClass", declaringClass)//
			.append("stereotype", stereotype)//
			.append("annotation", annotation)//
			.toString();
		}
	}

}
