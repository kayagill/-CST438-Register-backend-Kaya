package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;



@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "rest")
@RestController
public class GradebookServiceREST implements GradebookService {
	StudentRepository studentRepository;

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	private static String gradebook_url;

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
		Student stu = studentRepository.findByEmail(student_email);
		// TODO use RestTemplate to send message to gradebook service
		 EnrollmentDTO enrollmentDTO = new EnrollmentDTO( stu.getStudent_id(), student_email,  student_name,  course_id);
		 EnrollmentDTO response = restTemplate.postForObject(gradebook_url, enrollmentDTO, EnrollmentDTO.class);


		
	}
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	/*
	 * endpoint for final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody FinalGradeDTO[] grades, @PathVariable("course_id") int course_id) {
		System.out.println("Grades received "+grades.length);
		
		for (FinalGradeDTO finalGradeDTO : grades) {
	          
            Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId( finalGradeDTO.studentEmail(),finalGradeDTO.courseId());
            enrollment.setCourseGrade(finalGradeDTO.grade());

            // Save the updated enrollment entity
            enrollmentRepository.save(enrollment);

		}
		
		//TODO update grades in enrollment records with grades received from gradebook service
	}
}
