package com.cst438.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
@RestController
@CrossOrigin
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    //This line uses Spring's @Autowired annotation to inject an instance of 
    //the StudentRepository into the controller. 
    //This repository provides methods to interact with the database.
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return (List<Student>) studentRepository.findAll();
    }
    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }
    @PostMapping("/students")
    @Transactional
    public Student addStudent(@RequestBody Student student) {
        // You can add validation logic here if needed.
        return studentRepository.save(student);
    }
    @PutMapping("/students/{studentId}")
    @Transactional
    public Student updateStudent(@PathVariable Integer studentId, @RequestBody Student updatedStudent) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        // Update student details
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        // Add more fields to update as needed
        return studentRepository.save(existingStudent);
    }
    @DeleteMapping("/students/{studentId}")
    @Transactional
    public void deleteStudent(@PathVariable Integer studentId) {
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        studentRepository.delete(existingStudent);
    }
}







