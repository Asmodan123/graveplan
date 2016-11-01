package de.hannisoft.gaveplan.model;

public class Owner {
    private String saluation;
    private String saluationLetter;
    private String firstName;
    private String lastName;
    private String street;
    private String zipAndTown;

    public String getSaluation() {
        return saluation;
    }

    public void setSaluation(String saluation) {
        this.saluation = saluation;
    }

    public String getSaluationLetter() {
        return saluationLetter;
    }

    public void setSaluationLetter(String saluationLetter) {
        this.saluationLetter = saluationLetter;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipAndTown() {
        return zipAndTown;
    }

    public void setZipAndTown(String zipAndTown) {
        this.zipAndTown = zipAndTown;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
