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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * Resolvedor de Scanner
 * 
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 20/09/2011
 */
public interface AnnotationScannerResolver {
	/**
	 * Scaneia Fields retornando o nome da classe
	 * 
	 * @return Map[Annotation]=[Map[DeclaredType]=Set[fields]]
	 */
	Map<String, Map<String, Set<String>>> fields();

	/**
	 * Scaneia fields para o anotation
	 * 
	 * @param annotation
	 * @return Map[DeclaredType]=Set[fields]
	 */
	Map<String, Set<String>> fieldsOf(Class<? extends Annotation> annotation);

	/**
	 * Scaneia metodos
	 * 
	 * @return Map[Annotation]=[Map[DeclaredType]=Set[methods]]
	 */
	Map<String, Map<String, Set<String>>> methods();

	/**
	 * Scaneia metodos filtrando pelo annotation
	 * 
	 * @param annotation
	 * @return Map[DeclaredType]=Set[methods]
	 */
	Map<String, Set<String>> methodsOf(Class<? extends Annotation> annotation);

	/**
	 * Scaneia parametros
	 * 
	 * @return Map[Annotation]=[Map[DeclaredType]=[Map[DeclaredMethod]=Set[parameters
	 *         ]]]
	 */
	Map<String, Map<String, Map<String, Set<String>>>> parameters();

	/**
	 * Scaneia parametros filtrando pelo annotation
	 * 
	 * @return Map[DeclaredType]=[Map[DeclaredMethod]=Set[parameters]]
	 */
	Map<String, Map<String, Set<String>>> parametersOf(
			Class<? extends Annotation> annotation);

	/**
	 * Scaneia types filtrando pelo annotation
	 * 
	 * @return Map[Annotation]=Set[types]
	 */
	Map<String, Set<String>> types();

	/**
	 * Scaneia types filtrando pelo annotation
	 * 
	 * @return Set[types]
	 */
	Set<String> typesOf(Class<? extends Annotation> annotation);
}
