metatype
========
MetaType is APT processor that generate "Meta Classes" from your beans.
It provide a simple way to do bean reflection with some improvments :
* Check at compile time 
* Driect access to getters/setters
* Utils for some common algorithms
* Chained accessors 

How to use it ?
===============
With maven:
Add repository (the artifact is currently hosted in github repo)
```xml
<repository>
  <id>garc33-releases</id>
  <url>https://raw.github.com/garc33/m2p2-repository/mvn-repo/maven/releases/</url>
</repository>
```

Add dependency
```xml
<dependency>
  <groupId>fr.herman</groupId>
  <artifactId>metatype</artifactId>
  <version>0.0.2-SNAPSHOT</version>
</dependency>
```

Configure APT plugin
```xml
<plugin>
  <groupId>com.mysema.maven</groupId>
  <artifactId>apt-maven-plugin</artifactId>
  <version>1.1.0</version>
  <executions>
    <execution>
      <id>metatype</id>
      <goals>
        <goal>process</goal>
      </goals>
      <configuration>
        <outputDirectory>${project.build.directory}/generated-sources</outputDirectory>
        <processor>fr.herman.metatype.processor.MetaTypeProcessor</processor>
      </configuration>
    </execution>
  </executions>
</plugin>
```
Put `@MetaBean` annotation on your class (or interface)
```java
@MetaBean
public class Sample {
  private String hello;
  public String getHello(){
    return hello;
  }
  public void setHello(String hello){
    this.hello = hello;
  }
}
```
And a `SampleMeta` class will be generated !
