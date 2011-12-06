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

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 26/09/2011
 */
public class AnnotationScannerClassPathResolverException extends Exception {
	private static final long serialVersionUID = 7870527523871824368L;

	/**
	 * 
	 */
	public AnnotationScannerClassPathResolverException() {
	}

	/**
	 * @param message
	 */
	public AnnotationScannerClassPathResolverException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AnnotationScannerClassPathResolverException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public AnnotationScannerClassPathResolverException(final Throwable cause) {
		super(cause);
	}

}
