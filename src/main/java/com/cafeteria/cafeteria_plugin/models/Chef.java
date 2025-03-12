package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chefs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chef extends User {

    private String name;
}
