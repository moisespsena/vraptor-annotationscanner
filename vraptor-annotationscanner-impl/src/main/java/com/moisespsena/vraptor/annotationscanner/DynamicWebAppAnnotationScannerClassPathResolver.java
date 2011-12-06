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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

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
public class DynamicWebAppAnnotationScannerClassPathResolver implements
		AnnotationScannerClassPathResolver {
	private static final Logger logger = LoggerFactory
			.getLogger(DynamicWebAppAnnotationScannerClassPathResolver.class);

	private URL priorityUrl;

	private final ResourceURL resourceURL = new ResourceURL();

	private URL[] urls;

	private List<String> getPackagesFromPluginsJARs() {
		final List<String> result = new ArrayList<String>();
		// find plugin packages
		try {
			final Collection<URL> urls = resourceURL
					.getResourcesURL("META-INF/br.com.caelum.vraptor.packages");

			for (final URL url : urls) {
				final String packagesConfig = new Scanner(url.openStream())
						.useDelimiter("\\Z").next();
				if (packagesConfig != null) {
					Collections.addAll(result,
							packagesConfig.trim().split("\\s*,\\s*"));
				} else {
					logger.warn("Plugin packages file was empty: {}",
							url.getPath());
				}
			}
		} catch (final IOException e) {
			logger.error(
					"Exception while searching for packages file inside JARs",
					e);
		}

		return result;
	}

	private Collection<URL> getPackageURLs(final String basePackage) {
		final String resourcePath = basePackage.replace('.', '/');
		final Collection<URL> urls = resourceURL
				.getJarsURLFromResources(resourcePath);
		return urls;
	}

	@PostConstruct
	public void initialize() throws MalformedURLException {
		if (logger.isDebugEnabled()) {
			logger.debug("initializing");
		}

		final List<String> packages = getPackagesFromPluginsJARs();

		final List<URL> urlsList = new ArrayList<URL>();

		for (final String packageName : packages) {
			final Collection<URL> pkgUrls = getPackageURLs(packageName);
			urlsList.addAll(pkgUrls);
		}

		urls = urlsList.toArray(new URL[0]);

		for (final URL url : urls) {
			String urlStr = url.toExternalForm();
			final int pos = urlStr.indexOf("/WEB-INF/lib");
			if (pos > 0) {
				urlStr = urlStr.substring(0, pos + 9);
				urlStr += "classes";

				priorityUrl = new URL(urlStr);
				break;
			}
		}
	}

	@Override
	public URL prioritaryUrl()
			throws AnnotationScannerClassPathResolverException {
		return priorityUrl;
	}

	@Override
	public URL[] urls() {
		return urls;
	}
}
