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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zanata.annotationclaim.AnnotationClaim;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("WeakerAccess")
public class AnnotationClaimTest {

    @Mock
    ProcessingEnvironment processingEnv;
    @Mock
    RoundEnvironment roundEnv;
    String runWith = "org.junit.runner.RunWith";
    String cacheable = "javax.persistence.Cacheable";
    String annotationsOpt = runWith + ",\n    " + cacheable;
    @Mock
    TypeElement runWithElem;
    @Mock
    Name runWithName;
    @Mock
    TypeElement nullableElem;
    @Mock
    Name nullableName;

    AnnotationClaim claim;
    Map<String, String> options;

    @Before
    public void setUp() {
        claim = new AnnotationClaim();
        options = new HashMap<>();
        when(processingEnv.getOptions()).thenReturn(options);

        when(runWithElem.getQualifiedName()).thenReturn(runWithName);
        when(runWithName.toString()).thenReturn(runWith);

        when(nullableElem.getQualifiedName()).thenReturn(nullableName);
        when(nullableName.toString()).thenReturn("javax.annotation.Nullable");
    }

    @Test
    public void missingOption() throws Exception {
        claim.init(processingEnv);
        assertThat(claim.getSupportedAnnotationTypes()).isEqualTo(emptySet());
    }

    @Test
    public void normalCase() throws Exception {
        options.put(AnnotationClaim.OPT_VERBOSE, "true");
        options.put(AnnotationClaim.OPT_ANNOTATIONS, annotationsOpt);

        claim.init(processingEnv);

        assertThat(claim.getSupportedSourceVersion()).isEqualTo(
                SourceVersion.latest());
        assertThat(claim.getSupportedAnnotationTypes()).isEqualTo(
                new HashSet<>(asList(runWith, cacheable)));

        assertThat(claim.process(new HashSet<>(asList(runWithElem, nullableElem)), roundEnv)).isFalse();
        assertThat(claim.process(singleton(runWithElem), roundEnv)).isTrue();

    }

}
