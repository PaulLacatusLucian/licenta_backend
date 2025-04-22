package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherBriefDTO {
    private String name;
    private String subject;
    private String email;
}
