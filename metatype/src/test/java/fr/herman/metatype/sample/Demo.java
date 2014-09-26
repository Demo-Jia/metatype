package fr.herman.metatype.sample;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;
import fr.herman.metatype.annotation.MetaBean;
import fr.herman.metatype.annotation.MetaGetter;
import fr.herman.metatype.utils.Metas;

// @formatter:off
public class Demo
{
    @MetaBean
    public static class Person
    {
        private String firstName, lastName;
        private Address address;

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
        public Address getAddress()
        {
            return address;
        }
        public void setAddress(Address address)
        {
            this.address = address;
        }
    }

    @MetaBean
    public static class Child extends Person{
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

    @MetaBean
    public static class Address{
        private String street,city;

        public String getStreet(){
            return street;
        }

        public void setStreet(String street){
            this.street = street;
        }

        public String getCity(){
            return city;
        }

        public void setCity(String city){
            this.city = city;
        }
    }

    @Test
    public void test(){
        Person woman = new Person();
        woman.setFirstName("Jane");

        // value defaulting inline
        Metas.defaultValue(PersonMeta.$.firstName, woman, "John");
        assertEquals(woman.getFirstName(), "Jane");
        Metas.defaultValue(PersonMeta.$.lastName, woman, "Doe");
        assertEquals(woman.getLastName(), "Doe");

        // Bean to map
        Map<String, ?> map = Metas.toMap(woman, PersonMeta.$.firstName,PersonMeta.$.lastName);
        assertEquals(map.get("firstName"), "Jane");
        assertEquals(map.get(PersonMeta.$.lastName.name()), "Doe");

        //Customized property
        assertEquals(woman.initialsWithPointBetween(), "J.D");
        assertEquals(PersonMeta.$.initials.getValue(woman), "J.D");

        Person man = new Person();
        man.setFirstName("John");
        man.setLastName("Smith");

        //Copy property from an bean to anther
        Metas.copyValue(man, woman, PersonMeta.$.lastName);
        assertEquals(woman.getFirstName(), "Jane");
        assertEquals(woman.getLastName(), "Smith");

        //Copy values and handle inheritance
        Child child = new Child();
        Metas.copyValues(man, child, PersonMeta.$.firstName,PersonMeta.$.lastName);
        assertEquals(child.getFirstName(), "John");
        assertEquals(child.getLastName(), "Smith");

        List<Person> familly = asList(woman,man,child);

        //Collect
        Collection<String> initials = Metas.collect(familly,  PersonMeta.$.initials);
        assertEquals(initials, new ArrayList<>(asList("J.S","J.S","J.J.S")));

        //Distinct
        assertEquals(Metas.distinct(familly, PersonMeta.$.initials).size(), 2);

        //Frequency
        Map<String, Integer> frequency = Metas.frequency(familly, PersonMeta.$.firstName);
        assertEquals(frequency.get("John").intValue(), 2);
        assertEquals(frequency.get("Jane").intValue(), 1);

        Address address = new Address();
        address.setCity("Paris");

        //Batch set
        Metas.apply(PersonMeta.$.address, familly, address);
        Metas.apply(PersonMeta.$.address.street, familly, asList("Provence","La Fayette","Victoire"));

        //Fluent
        assertEquals(PersonMeta.$.address.city.getValue(man),"Paris");

    }
}
