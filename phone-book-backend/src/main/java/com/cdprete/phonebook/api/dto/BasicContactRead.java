package com.cdprete.phonebook.api.dto;

import java.io.Serial;

/**
 * @author Cosimo Damiano Prete
 * @since 02/02/2022
 */
public class BasicContactRead extends Identifiable  {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String surname;
    private byte[] image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
