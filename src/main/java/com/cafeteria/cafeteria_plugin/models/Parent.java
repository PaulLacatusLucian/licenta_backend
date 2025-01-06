package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Parent extends User {

    private String motherName;
    private String motherEmail;
    private String motherPhoneNumber;

    private String fatherName;
    private String fatherEmail;
    private String fatherPhoneNumber;
}

