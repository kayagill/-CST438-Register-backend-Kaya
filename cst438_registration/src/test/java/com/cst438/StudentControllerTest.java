package com.cst438;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.StudentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void createStudent() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// retrieve the student
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/"+student_id)
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(sdto.name(), actual.name());
		assertEquals(sdto.email(), actual.email());
		assertEquals(sdto.statusCode(), actual.statusCode());
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void createStudentDupEmail() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// try to create another student with same email
		sdto = new StudentDTO(0, "name2 test2", "ntest@csumb.edu", 0, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus()); // BAD_REQUEST
		assertTrue(response.getErrorMessage().contains("student email already exists"));
		
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void updateStudent() throws Exception  {
		
		MockHttpServletResponse response;

		// retrieve the student id = 2
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/2")
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO original = fromJsonString(response.getContentAsString(), StudentDTO.class);
		// modify name, email and statusCode
		StudentDTO mod = new StudentDTO(original.studentId(), "new name", "newname@csumb.edu", 1, "balance outstanding");
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/2")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(mod)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		// retrieve again and check updated fields
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/2")
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(mod, actual);
	}
	
	@Test
	public void updateStudentDupEmail() throws Exception {
		// create 2 students 
		
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		StudentDTO sdto2 = new StudentDTO(0, "name test2", "ntest2@csumb.edu", 0, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto2)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id2 = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id2 > 0);
		
		// attempt to change email of student #1 to student #2
		StudentDTO sdto3 = new StudentDTO(student_id, "name test", "ntest2@csumb.edu", 0, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/"+student_id)
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto3)))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus());
		assertTrue(response.getErrorMessage().contains("student email already exists"));
		
	}
	
	@Test
	public void updateStudentNotFound() throws Exception {
		StudentDTO sdto = new StudentDTO(99, "namenew test", "ntestnew@csumb.edu", 0, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/99")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(404, response.getStatus());
		
	}
	
	@Test
	public void deleteStudentNoEnrollments() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		// another delete should be OK.
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void deleteStudentWithEnrollment() throws Exception {
		MockHttpServletResponse response;
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/1"))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus()); // BAD_REQUEST
		assertTrue(response.getErrorMessage().contains("student has enrollments"));
		
		// now do a force delete
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/1?force=yes"))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());	
		
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