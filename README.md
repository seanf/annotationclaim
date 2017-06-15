AnnotationClaim
===============
AnnotationClaim is a simple annotation processor for the Java compiler,
which simply "claims" whichever annotations you specify. This makes it
possible to avoid the `javac` Xlint warning: `No processor claimed any of these
annotations: org.example.Annotation` when using javac's `-Xlint` option
together with (a) other annotation processors and (b) other "runtime" annotations.

This is useful if you are trying to eliminate all compiler warnings, at least
until https://bugs.openjdk.java.net/browse/JDK-6999068 is fixed.


Usage
=====

Specify the annotation option `org.zanata.annotationclaim.annotations` with a
list of annotation names you want to claim. For instance:

```
javac Example.java -processorpath annotationclaim.jar \
  -Aorg.zanata.annotationclaim.annotations=org.junit.runner.RunWith,org.junit.Test
```

If you are using Maven or Gradle to compile, you may wish to use a project
property to hold the list of annotations. You can separate multiple
annotations with commas or whitespace characters, including newlines.

You can also specify the additional annotation `org.zanata.annotationclaim.verbose=true`
to enable more logging. Note that this may trigger a minor but annoying
warning bug in the compiler: https://bugs.openjdk.java.net/browse/JDK-8162455


How it works
------------
AnnotationClaim will tell `javac` to send it whichever annotations you tell
it. When `javac` comes across one of these annotations, it will pass it to
AnnotationClaim, and AnnotationClaim will report to `javac` that the annotation
has been claimed, ie processed. This will prevent the annotation from being
seen by any other annotation processor, so make sure the annotation is not
needed by your other annotation processors before telling AnnotationClaim to
claim it. As long as every annotation has been claimed by an annotation
processor, you should avoid the Xlint warning `No processor claimed any of these
annotations: org.example.Annotation`.


Shortcomings
------------
You have to list all of the runtime annotations for which you want to suppress
the warning, and there are often a lot of them. It would be better if you
could just list the small number of annotations which your other annotation
processors want to use at compile time, but this would require AnnotationClaim
to declare support for wildcard annotations, `javac` would pass *all*
annotations on a code element to AnnotationClaim, and then AnnotationClaim
could (a) claim them all (thus preventing other processors from working) or
(b) not claim them all (thus failing to prevent the "No processor claimed..."
warning).

Hard-coding a list of "runtime" annotations into AnnotationClaim would reduce
the need for large lists, but would increase the maintenance burden of
AnnotationClaim, and also increase the likelihood of blocking an important
annotation. For instance, the annotation `javax.annotation.Nullable` is
*typically* not used by any compile-time annotation processor, but this is not
true for projects which use the Checker Framework's Nullness Checker.


Updating the annotation list
----------------------------
When `javac` outputs a warning for one or more new runtime annotations -

```
warning: No processor claimed any of these annotations: org.example.Annotation1,org.example.Annotation2
```

- you can just add the listed annotations to the `org.zanata.annotationclaim.annotations`
parameter. But before you do, please **make sure the annotations don't belong to
a new annotation processor** which you forgot to activate, or you will stop
that other annotation processor from doing its job.


Example
-------
```
# Compiling with an annotation processor, but without activating the AnnotationClaim processor:
$ javac src/main/java/org/zanata/annotationclaim/AnnotationClaim.java \
  -Xlint -processorpath ~/.m2/repository/org/projectlombok/lombok/1.16.6/lombok-1.16.6.jar
warning: No processor claimed any of these annotations: javax.annotation.processing.SupportedOptions
1 warning

$

# With the AnnotationClaim processor activated:
$ javac src/main/java/org/zanata/annotationclaim/AnnotationClaim.java \
  -Xlint -processorpath ~/.m2/repository/org/projectlombok/lombok/1.16.6/lombok-1.16.6.jar:build/libs/annotationclaim-1.0-SNAPSHOT.jar \
  -Aorg.zanata.annotationclaim.annotations=javax.annotation.processing.SupportedOptions

$
```


More Details
------------
See https://bugs.openjdk.java.net/browse/JDK-6999068 for more details about
the javac warning.


Maven Example with Takari Lifecycle Plugin
------------------------------------------

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
...

  <properties>
    <annotationclaim.annotations>
      com.google.common.annotations.Beta
      com.google.common.annotations.VisibleForTesting
      com.google.gwt.user.client.rpc.RemoteServiceRelativePath
      com.google.gwtmockito.GwtMock
      com.tngtech.java.junit.dataprovider.DataProvider
      com.tngtech.java.junit.dataprovider.UseDataProvider
      edu.umd.cs.findbugs.annotations.SuppressFBWarnings
      java.beans.ConstructorProperties
      java.lang.FunctionalInterface
      javax.annotation.CheckForNull
      javax.annotation.Nonnull
      javax.annotation.Nullable
      javax.annotation.ParametersAreNonnullByDefault
      javax.annotation.PostConstruct
      javax.annotation.PreDestroy
      javax.annotation.Priority
      javax.annotation.Resource
      javax.ejb.MessageDriven
      javax.enterprise.context.ApplicationScoped
      javax.enterprise.context.Dependent
      javax.enterprise.context.RequestScoped
      javax.enterprise.context.SessionScoped
      javax.enterprise.event.Observes
      javax.enterprise.inject.Alternative
      javax.enterprise.inject.Any
      javax.enterprise.inject.Default
      javax.enterprise.inject.Disposes
      javax.enterprise.inject.Model
      javax.enterprise.inject.Produces
      javax.enterprise.inject.Stereotype
      javax.enterprise.util.Nonbinding
      javax.faces.application.ResourceDependencies
      javax.faces.bean.ViewScoped
      javax.faces.component.FacesComponent
      javax.faces.render.FacesRenderer
      javax.inject.Inject
      javax.inject.Named
      javax.inject.Qualifier
      javax.interceptor.AroundInvoke
      javax.interceptor.Interceptor
      javax.interceptor.InterceptorBinding
      javax.persistence.Access
      javax.persistence.Basic
      javax.persistence.Cacheable
      javax.persistence.Column
      javax.persistence.DiscriminatorColumn
      javax.persistence.DiscriminatorValue
      javax.persistence.ElementCollection
      javax.persistence.Embeddable
      javax.persistence.EmbeddedId
      javax.persistence.Entity
      javax.persistence.EntityListeners
      javax.persistence.Enumerated
      javax.persistence.GeneratedValue
      javax.persistence.Id
      javax.persistence.IdClass
      javax.persistence.Inheritance
      javax.persistence.JoinColumn
      javax.persistence.JoinTable
      javax.persistence.Lob
      javax.persistence.ManyToMany
      javax.persistence.ManyToOne
      javax.persistence.MapKey
      javax.persistence.MapKeyColumn
      javax.persistence.MapKeyEnumerated
      javax.persistence.MapKeyJoinColumn
      javax.persistence.MappedSuperclass
      javax.persistence.NamedQueries
      javax.persistence.OneToMany
      javax.persistence.OneToOne
      javax.persistence.OrderBy
      javax.persistence.OrderColumn
      javax.persistence.PersistenceUnit
      javax.persistence.PostLoad
      javax.persistence.PostPersist
      javax.persistence.PostUpdate
      javax.persistence.PrePersist
      javax.persistence.PreRemove
      javax.persistence.PreUpdate
      javax.persistence.Table
      javax.persistence.Temporal
      javax.persistence.Transient
      javax.persistence.Version
      javax.servlet.annotation.WebFilter
      javax.servlet.annotation.WebServlet
      javax.validation.Constraint
      javax.validation.constraints.NotNull
      javax.validation.constraints.Pattern
      javax.validation.constraints.Size
      javax.ws.rs.ApplicationPath
      javax.ws.rs.ConstrainedTo
      javax.ws.rs.Consumes
      javax.ws.rs.DELETE
      javax.ws.rs.DefaultValue
      javax.ws.rs.GET
      javax.ws.rs.HeaderParam
      javax.ws.rs.POST
      javax.ws.rs.PUT
      javax.ws.rs.Path
      javax.ws.rs.PathParam
      javax.ws.rs.Produces
      javax.ws.rs.QueryParam
      javax.ws.rs.container.PreMatching
      javax.ws.rs.core.Context
      javax.ws.rs.ext.Provider
      javax.xml.bind.annotation.XmlAttribute
      javax.xml.bind.annotation.XmlElement
      javax.xml.bind.annotation.XmlElementRef
      javax.xml.bind.annotation.XmlRootElement
      javax.xml.bind.annotation.XmlType
      org.apache.deltaspike.core.api.common.DeltaSpike
      org.apache.deltaspike.core.api.exception.control.ExceptionHandler
      org.apache.deltaspike.core.api.exception.control.Handles
      org.apache.deltaspike.core.api.exclude.Exclude
      org.apache.deltaspike.core.api.lifecycle.Destroyed
      org.apache.deltaspike.core.api.lifecycle.Initialized
      org.apache.deltaspike.core.api.scope.ConversationGroup
      org.apache.deltaspike.core.api.scope.GroupedConversationScoped
      org.apache.deltaspike.core.api.scope.WindowScoped
      org.apache.deltaspike.jpa.api.transaction.Transactional
      org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseListener
      org.apache.deltaspike.scheduler.api.Scheduled
      org.apache.deltaspike.security.api.authorization.Secured
      org.apache.deltaspike.security.api.authorization.Secures
      org.apache.deltaspike.security.api.authorization.SecurityBindingType
      org.codehaus.jackson.annotate.JsonIgnore
      org.codehaus.jackson.annotate.JsonIgnoreProperties
      org.codehaus.jackson.annotate.JsonProperty
      org.codehaus.jackson.annotate.JsonPropertyOrder
      org.codehaus.jackson.map.annotate.JsonSerialize
      org.hibernate.annotations.AttributeAccessor
      org.hibernate.annotations.BatchSize
      org.hibernate.annotations.Cache
      org.hibernate.annotations.Immutable
      org.hibernate.annotations.ListIndexBase
      org.hibernate.annotations.NaturalId
      org.hibernate.annotations.Type
      org.hibernate.annotations.TypeDef
      org.hibernate.annotations.TypeDefs
      org.hibernate.annotations.Where
      org.hibernate.search.annotations.AnalyzerDefs
      org.hibernate.search.annotations.AnalyzerDiscriminator
      org.hibernate.search.annotations.ClassBridge
      org.hibernate.search.annotations.Factory
      org.hibernate.search.annotations.Field
      org.hibernate.search.annotations.FieldBridge
      org.hibernate.search.annotations.FullTextFilterDef
      org.hibernate.search.annotations.Indexed
      org.hibernate.search.annotations.IndexedEmbedded
      org.hibernate.search.annotations.SortableField
      org.hibernate.validator.constraints.Email
      org.hibernate.validator.constraints.NotEmpty
      org.jboss.arquillian.container.test.api.Deployment
      org.jboss.arquillian.container.test.api.RunAsClient
      org.jboss.arquillian.core.api.annotation.Inject
      org.jboss.arquillian.core.api.annotation.Observes
      org.jboss.arquillian.test.api.ArquillianResource
      org.jboss.arquillian.test.spi.annotation.ClassScoped
      org.jboss.arquillian.test.spi.annotation.TestScoped
      org.jboss.resteasy.annotations.providers.jaxb.Wrapped
      org.jboss.resteasy.annotations.providers.multipart.MultipartForm
      org.jglue.cdiunit.AdditionalClasses
      org.jglue.cdiunit.AdditionalClasspaths
      org.jglue.cdiunit.InRequestScope
      org.jglue.cdiunit.InSessionScope
      org.jglue.cdiunit.ProducerConfig
      org.jglue.cdiunit.ProducesAlternative
      org.jglue.cdiunit.deltaspike.SupportDeltaspikeCore
      org.junit.After
      org.junit.AfterClass
      org.junit.Before
      org.junit.BeforeClass
      org.junit.ClassRule
      org.junit.Ignore
      org.junit.Rule
      org.junit.Test
      org.junit.experimental.categories.Categories.IncludeCategory
      org.junit.experimental.categories.Category
      org.junit.runner.RunWith
      org.junit.runners.Parameterized.Parameter
      org.junit.runners.Parameterized.Parameters
      org.junit.runners.Parameterized.UseParametersRunnerFactory
      org.junit.runners.Suite.SuiteClasses
      org.mockito.Captor
      org.mockito.Mock
      org.ocpsoft.rewrite.annotation.RewriteConfiguration
    </annotationclaim.annotations>
  </properties>

  <build>
    <plugins>
      <plugin>
        <groupId>io.takari.maven.plugins</groupId>
        <artifactId>takari-lifecycle-plugin</artifactId>
        <extensions>true</extensions>
        <configuration>
          <annotationProcessorOptions>
            <org.zanata.annotationclaim.annotations>${annotationclaim.annotations}</org.zanata.annotationclaim.annotations>
          </annotationProcessorOptions>
          <proc>proc</proc>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>org.zanata</groupId>
            <artifactId>annotationclaim</artifactId>
            <version>1.0</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>
...
</project>
```
