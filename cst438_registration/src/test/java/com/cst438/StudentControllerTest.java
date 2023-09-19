package com.cst438;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentControllerTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllStudents() {
        // Mock data
        List<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.setName("Nik");
        student1.setEmail("nik@csumb.edu");
        student1.setStatus("0");
        student1.setStatusCode(0);
        student1.setStudent_id(123);
        students.add(student1);
        Student student2 = new Student();
        student2.setName("Kylee");
        student2.setEmail("kylee@csumb.edu");
        student2.setStatus("0");
        student2.setStatusCode(0);
        student2.setStudent_id(143);
        students.add(student2);
        

        // Mock the repository's behavior
        when(studentRepository.findAll()).thenReturn(students);

        // Test the controller method
        List<Student> result = studentController.getAllStudents();

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Nik", result.get(0).getName());
        assertEquals("Kylee", result.get(1).getName());
    }

    @Test
    public void testGetStudentById() {
        // Mock data
        Student student = new Student();
        student.setName("Nik");
        student.setEmail("nik@csumb.edu");
        student.setStatus("0");
        student.setStatusCode(0);
        student.setStudent_id(123);

        // Mock the repository's behavior
        when(studentRepository.findById(123)).thenReturn(Optional.of(student));
        when(studentRepository.findById(2)).thenReturn(Optional.empty());

        // Test the controller method
        Student result1 = studentController.getStudentById(123);
     

        // Assertions
        assertNotNull(result1);
        assertEquals("Nik", result1.getName());
        assertEquals("nik@csumb.edu", result1.getEmail());
        assertEquals("0", result1.getStatus());
        assertEquals(0, result1.getStatusCode());
        assertEquals(123, result1.getStudent_id());

        assertThrows(ResponseStatusException.class, () -> {
            studentController.getStudentById(3); // Student with ID 3 does not exist
        });
    }
   

    @Test
    public void testAddStudent() {
        // Mock data
    	Student student1 = new Student();
        student1.setName("Cole");
        student1.setEmail("cole@csumb.edu");
        student1.setStatus("0");
        student1.setStatusCode(0);
        student1.setStudent_id(321);

        // Mock the repository's behavior
        when(studentRepository.save(student1)).thenReturn(student1);

        // Test the controller method
        Student result = studentController.addStudent(student1);

        // Assertions
        assertNotNull(result);
        assertEquals("Cole", result.getName());
    }

    @Test
    public void testUpdateStudent() {
        // Mock data
Student existingStudent = new Student();
        
        existingStudent.setName("Nik");
        existingStudent.setEmail("nik@csumb.edu");
        existingStudent.setStatus("0");
        existingStudent.setStatusCode(0);
        existingStudent.setStudent_id(123);

        Student updatedStudent = new Student();

        
        updatedStudent.setName("Nik L");
        updatedStudent.setEmail("nik@csumb.edu");
        updatedStudent.setStatus("0");
        updatedStudent.setStatusCode(0);
        updatedStudent.setStudent_id(123);


        // Mock the repository's behavior
        when(studentRepository.findById(1)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(existingStudent)).thenReturn(updatedStudent);

        // Test the controller method
        Student result = studentController.updateStudent(1, updatedStudent);

        // Assertions
        assertNotNull(result);
        assertEquals("Nik L", result.getName());
    }

    @Test
    public void testDeleteStudent() {
        // Mock data
        Student existingStudent = new Student();
        
        existingStudent.setName("Nik");
        existingStudent.setEmail("nik@csumb.edu");
        existingStudent.setStatus("0");
        existingStudent.setStatusCode(0);
        existingStudent.setStudent_id(123);

        // Mock the repository's behavior
        when(studentRepository.findById(1)).thenReturn(Optional.of(existingStudent));

        // Test the controller method
        assertDoesNotThrow(() -> {
            studentController.deleteStudent(1);
        });

        // Verify that the delete method was called once
        verify(studentRepository, times(1)).delete(existingStudent);
    }
}

