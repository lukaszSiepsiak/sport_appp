package com.example.npi.mobile.json;

import android.graphics.Bitmap;

import java.util.List;

public class SportObject {
    private Account account;

    private String name;

    private String address;

    private String postalCode;

    private String description;

    private String voivodeship;

    private String city;

    private String street;

    private Bitmap icon;
    private List<String> objectOffer;

    public List<String> getObjectOffer() {
        return objectOffer;
    }

    public void setObjectOffer(List<String> objectOffer) {
        this.objectOffer = objectOffer;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    private String discipline;


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }


    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }
    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
