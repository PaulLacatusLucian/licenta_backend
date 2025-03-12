package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin extends User {

    public Admin() {
        super();
        this.setUserType(UserType.ADMIN);
    }
}
