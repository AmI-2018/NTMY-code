// Not implemented yet
package io.ami2018.ntmy;

public class Person {

    private String name;
    private String surname;
    private String image;
    private String email;
    private String phone;

    public Person(
            String name,
            String surname,
            String image,
            String email,
            String phone) {

        this.name = name;
        this.surname = surname;
        this.image = image;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

}