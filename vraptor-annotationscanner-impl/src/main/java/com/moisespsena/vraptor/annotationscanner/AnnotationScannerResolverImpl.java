/***
 * Copyright (c) 2011 Moises P. Sena - www.moisespsena.com
 * All rights reserved.
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
/**
 * 
 */
package com.moisespsena.vraptor.annotationscanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.scannotation.AnnotationDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 26/09/2011
 */
@Component
@ApplicationScoped
public class AnnotationScannerResolverImpl implements AnnotationScannerResolver {
	private static final Logger logger = LoggerFactory
			.getLogger(AnnotationScannerResolverImpl.class);

	private static AnnotationDB createAnnotationDb(final boolean classes,
			final boolean fields, final boolean methods,
			final boolean parameters) {
		final AnnotationDB annotationDB = new AnnotationDB();
		annotationDB.setScanClassAnnotations(classes);
		annotationDB.setScanFieldAnnotations(fields);
		annotationDB.setScanMethodAnnotations(methods);
		annotationDB.setScanParameterAnnotations(parameters);

		return annotationDB;
	}

	private final AnnotationScannerClassPathResolver classPathResolver;

	private Map<String, Set<String>> types;

	/**
	 * 
	 */
	public AnnotationScannerResolverImpl(
			final AnnotationScannerClassPathResolver classPathResolver) {
		this.classPathResolver = classPathResolver;
	}

	@Override
	public Map<String, Map<String, Set<String>>> fields() {
		return null;
	}

	@Override
	public Map<String, Set<String>> fieldsOf(
			final Class<? extends Annotation> annotation) {
		return null;
	}

	@PostConstruct
	public void initialize() throws IOException,
			AnnotationScannerClassPathResolverException {
		if (logger.isDebugEnabled()) {
			logger.debug("initializing");
		}

		final URL[] urls = classPathResolver.urls();
		final URL priorityUrl = classPathResolver.prioritaryUrl();
		final AnnotationDB annotationDB = createAnnotationDb(true, false,
				false, false);
		annotationDB.scanArchives(urls);
		annotationDB.scanArchives(priorityUrl);

		final Map<String, Set<String>> index = annotationDB
				.getAnnotationIndex();

		types = index;
	}

	@Override
	public Map<String, Map<String, Set<String>>> methods() {
		return null;
	}

	@Override
	public Map<String, Set<String>> methodsOf(
			final Class<? extends Annotation> annotation) {
		return null;
	}

	@Override
	public Map<String, Map<String, Map<String, Set<String>>>> parameters() {
		return null;
	}

	@Override
	public Map<String, Map<String, Set<String>>> parametersOf(
			final Class<? extends Annotation> annotation) {
		return null;
	}

	@Override
	public Map<String, Set<String>> types() {
		return types;
	}

	@Override
	public Set<String> typesOf(final Class<? extends Annotation> annotation) {
		return types.get(annotation.getName());
	}
}
