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
public class Person
{
    private String firstName, lastName;

    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    //customize property
    @MetaGetter("initials")
    public String initialsWithPointBetween(){
        return firstName.substring(0, 1) + '.' + lastName.substring(0, 1);
    }
}
@MetaBean
public class Child extends Person{
    private final String suffix="Jr";

    public String getSuffix(){
        return suffix;
    }
    @Override
    public String initialsWithPointBetween(){
        return getFirstName().substring(0, 1)+
            '.'+suffix.substring(0, 1) + '.' +
                getLastName().substring(0, 1);
    }
}
```
`PersonMeta` and `ChildMeta` class will be generated !
You can use them like this :
```java
Person woman = new Person();
woman.setFirstName("Jane");

// value defaulting inline
Metas.defaultValue(PersonMeta.firstName, woman, "John");
assertEquals(woman.getFirstName(), "Jane");
Metas.defaultValue(PersonMeta.lastName, woman, "Doe");
assertEquals(woman.getLastName(), "Doe");

// Bean to map
Map<String, ?> map = Metas.toMap(woman, PersonMeta.firstName,PersonMeta.lastName);
assertEquals(map.get("firstName"), "Jane");
assertEquals(map.get(PersonMeta.lastName.name()), "Doe");

//Customized property
assertEquals(woman.initialsWithPointBetween(), "J.D");
assertEquals(PersonMeta.initials.getValue(woman), "J.D");

Person man = new Person();
man.setFirstName("John");
man.setLastName("Smith");

//Copy property from an bean to anther
Metas.copyValue(man, woman, PersonMeta.lastName);
assertEquals(woman.getFirstName(), "Jane");
assertEquals(woman.getLastName(), "Smith");

//Copy values and handle inheritance
Child child = new Child();
Metas.copyValues(man, child, PersonMeta.firstName,PersonMeta.lastName);
assertEquals(child.getFirstName(), "John");
assertEquals(child.getLastName(), "Smith");

List<Person> familly = Arrays.asList(woman,man,child);

//Collect
Collection<String> initials = Metas.collect(familly,  PersonMeta.initials);
assertEquals(initials, new ArrayList<>(Arrays.asList("J.S","J.S","J.J.S")));

//Distinct
assertEquals(Metas.distinct(familly, PersonMeta.initials).size(), 2);

//Frequency
Map<String, Integer> frequency = Metas.frequency(familly, PersonMeta.firstName);
assertEquals(frequency.get("John").intValue(), 2);
assertEquals(frequency.get("Jane").intValue(), 1);
```
