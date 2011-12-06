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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.vidageek.mirror.dsl.Mirror;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 26/09/2011
 */
public class ResourceURL {
	public String absolutePathFrom(final URL url) {
		try {
			final URLConnection connection = url.openConnection();
			final Class<?> clazz = Class.forName("org.jboss.vfs.VirtualFile");

			final Object content = connection.getContent();
			final File file = (File) new Mirror().on(clazz).reflect()
					.method("getPhysicalFile").withoutArgs().invoke(content);
			return file.getAbsolutePath();
		} catch (final ClassNotFoundException e) {
			return url.getFile();
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void doUnzip(final String inputZip,
			final String destinationDirectory) throws IOException {
		final int BUFFER = 2048;
		final List zipFiles = new ArrayList();
		final File sourceZipFile = new File(inputZip);
		final File unzipDestinationDirectory = new File(destinationDirectory);
		unzipDestinationDirectory.mkdir();

		ZipFile zipFile;
		// Open Zip file for reading
		zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

		// Create an enumeration of the entries in the zip file
		final Enumeration zipFileEntries = zipFile.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			final ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

			final String currentEntry = entry.getName();

			final File destFile = new File(unzipDestinationDirectory,
					currentEntry);

			if (currentEntry.endsWith(".zip")) {
				zipFiles.add(destFile.getAbsolutePath());
			}

			// grab file's parent directory structure
			final File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();
			try {
				// extract file if not a directory
				if (!entry.isDirectory()) {
					final BufferedInputStream is = new BufferedInputStream(
							zipFile.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					final byte data[] = new byte[BUFFER];

					// write the current file to disk
					final FileOutputStream fos = new FileOutputStream(destFile);
					final BufferedOutputStream dest = new BufferedOutputStream(
							fos, BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				} else {
					final File dir = new File(unzipDestinationDirectory,
							entry.getName());
					if (!dir.exists() && !dir.mkdirs()) {

					}
				}
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}
		zipFile.close();

		for (final Iterator iter = zipFiles.iterator(); iter.hasNext();) {
			final String zipName = (String) iter.next();
			doUnzip(zipName, destinationDirectory + File.separatorChar
					+ zipName.substring(0, zipName.lastIndexOf(".zip")));
		}

	}

	public URL getClassesURL() {
		return getResourceURL("/WEB-INF/classes");
	}

	public String getContextDirectory() {
		final String path = ResourceURL.class.getName().replace('.', '/')
				+ ".class";
		final URL url = Thread.currentThread().getContextClassLoader()
				.getResource(path);

		final String ctxPath = absolutePathFrom(url).replace(
				"/WEB-INF/classes/" + path, "");

		return ctxPath;
	}

	public String getContextResourcePath(final String resourcePath) {
		return getContextDirectory() + resourcePath;
	}

	private String getJarFromResource(final String resource) {
		String path = resource.replaceAll("^(\\w+:)+/", "/");
		final int pos = path.indexOf('!');

		if (pos > -1) {
			path = path.substring(0, pos);
		}

		return path;
	}

	public Collection<URL> getJarsURLFromResources(final String resourcePath) {
		final Collection<URL> urls = getResourcesURL(resourcePath, true);
		return urls;
	}

	public URL getLibURL() {
		return getResourceURL("/WEB-INF/lib");
	}

	public Collection<URL> getResourcesURL(final String resourcePath) {
		return getResourcesURL(resourcePath, false);
	}

	private Collection<URL> getResourcesURL(final String resourcePath,
			final boolean someJar) {
		try {
			final Enumeration<URL> urls = Thread.currentThread()
					.getContextClassLoader().getResources(resourcePath);

			final List<URL> lurls = new ArrayList<URL>();

			for (; urls.hasMoreElements();) {
				final String protocol = "file:";
				final URL url = urls.nextElement();
				String urlStr = "";

				String path = absolutePathFrom(url);
				if (path.contains(".jar!/")) {
					if (someJar) {
						path = getJarFromResource(path);
						path = protocol + path;
					} else if (path.startsWith(protocol)) {
						path = path.replace(protocol, "jar:file:");
					}
					urlStr = path;
				} else {

					// from jboss path
					if (path.contains(".jar-")) {
						final String extractDir = path.replaceAll(
								"/(([^/]+.jar)-\\w+/contents/).*", "/$1");
						final String jarFilePath = path.replaceAll(
								"/(([^/]+.jar)-\\w+)/.*", "/$1/$2");

						doUnzip(jarFilePath, extractDir);
					}

					if (someJar) {
						urlStr = protocol + getJarFromResource(path);
					} else {
						urlStr = protocol + path;
					}
				}

				final URL nURL = new URL(urlStr);
				lurls.add(nURL);
			}
			return lurls;
		} catch (final MalformedURLException e) {

			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public URL getResourceURL(final String resourcePath) {
		try {
			return new URL("file:" + getContextResourcePath(resourcePath));
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public URL getWebXMLURL() {
		return getResourceURL("/WEB-INF/web.xml");
	}
}
