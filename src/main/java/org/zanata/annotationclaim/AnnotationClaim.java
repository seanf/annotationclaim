/*
 * Copyright 2017 Red Hat Inc. and individual contributors identified by
 * the @authors tag. See the copyright.txt in the distribution for a full
 * listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zanata.annotationclaim;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import static java.lang.Boolean.parseBoolean;
import static java.util.Collections.addAll;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
@SupportedOptions({ AnnotationClaim.OPT_ANNOTATIONS })
public class AnnotationClaim extends AbstractProcessor {
    static final String OPT_ANNOTATIONS =
            "org.zanata.annotationclaim.annotations";
    static final String OPT_VERBOSE =
            "org.zanata.annotationclaim.verbose";
    private Set<String> annotationsToClaim;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return annotationsToClaim;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Map<String, String> options = processingEnv.getOptions();
        String annoOption = options.get(OPT_ANNOTATIONS);
        boolean verbose = parseBoolean(options.get(OPT_VERBOSE));
        if (verbose) {
            System.out.println("AnnotationClaimer is active.");
        }
        super.init(processingEnv);
        Set<String> annotationsToClaim = new HashSet<>();
        if (annoOption != null) {
            String[] annotations = annoOption.trim().split("[,\\s]+");
            addAll(annotationsToClaim, annotations);
            if (verbose) {
                System.out.println("AnnotationClaimer claiming annotations:");
                annotationsToClaim.forEach(it -> System.out.println("  " + it));
            }
        } else {
            System.err.println("AnnotationClaimer not claiming annotations: " +
                    OPT_ANNOTATIONS + " is not set.");
        }
        this.annotationsToClaim = unmodifiableSet(annotationsToClaim);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        Set<String> names =
                annotations.stream().map(it -> it.getQualifiedName().toString())
                        .collect(toSet());
        return annotationsToClaim.containsAll(names);
    }
}
