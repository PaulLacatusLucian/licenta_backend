package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.ParentMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        List<ParentDTO> parentDTOs = parentService.getAllParents()
                .stream()
                .map(parentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(parentDTOs);
    }


    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        Parent parent = parentService.getParentById(id);
        return ResponseEntity.ok(parentMapper.toDto(parent));
    }


    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ParentDTO> updateParent(@PathVariable Long id, @RequestBody ParentDTO parentDTO) {
        Parent updatedParent = parentService.updateParent(id, parentMapper.toEntity(parentDTO));
        return ResponseEntity.ok(parentMapper.toDto(updatedParent));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('PARENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/me/child")
    public ResponseEntity<?> getStudentForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> ResponseEntity.ok(studentMapper.toDTO(student)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StudentDTO()));

    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/me/add-student")
    public ResponseEntity<?> addStudentToParent(@RequestHeader("Authorization") String token, @RequestBody Student student) {
        try {
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            Parent parent = parentService.findByUsername(username);

            Student added = parentService.addStudentToParent(parent.getId(), student);
            return ResponseEntity.ok(added);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
