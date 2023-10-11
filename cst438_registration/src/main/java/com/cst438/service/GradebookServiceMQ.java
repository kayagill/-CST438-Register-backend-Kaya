package com.cst438.service;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "mq")
public class GradebookServiceMQ implements GradebookService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	StudentRepository studentRepository;
	

	Queue gradebookQueue = new Queue("gradebook-queue", true);

	 

	 @Bean

	 Queue createQueue() {

	 return new Queue("registration-queue");

	 }

	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
		// create EnrollmentDTO, convert to JSON string and send to gradebookQueue
		// TODO
		Student stu = studentRepository.findByEmail(student_email);
        // Create an EnrollmentDTO
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO(stu.getStudent_id(), student_email, student_name, course_id);

  
            // Convert the EnrollmentDTO to a JSON string
            String enrollmentJson = asJsonString(enrollmentDTO);

            // Send the enrollment message to the gradebookQueue
            rabbitTemplate.convertAndSend("gradebook-queue", enrollmentJson);

        
	}
	
	@RabbitListener(queues = "registration-queue")
	@Transactional
	public void receive(String message) {
		System.out.println("Receive grades :" + message);
		FinalGradeDTO[] finalGradeDTOs = fromJsonString(message, FinalGradeDTO[].class);
		for (FinalGradeDTO finalGradeDTO : finalGradeDTOs) {
          
            Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId( finalGradeDTO.studentEmail(),finalGradeDTO.courseId());
            enrollment.setCourseGrade(finalGradeDTO.grade());

            // Save the updated enrollment entity
            enrollmentRepository.save(enrollment);

		}

		/*
		 * for each student grade in courseDTOG,  find the student enrollment 
		 * entity and update the grade.
		 */
		
		// deserialize the string message to FinalGradeDTO[] 
		
		// TODO

	}
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
