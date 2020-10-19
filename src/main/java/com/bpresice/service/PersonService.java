package com.bpresice.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bpresice.entities.Counters;
import com.bpresice.entities.Employee;
import com.bpresice.entities.Manager;
import com.bpresice.entities.Person;
import com.bpresice.entities.Report;
import com.bpresice.entities.Task;
import com.bpresice.repository.PersonRepository;
import com.mongodb.client.result.UpdateResult;

@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;

	public <T extends Person> T save(T document) {
		return personRepository.save(document);
	}

	public UpdateResult updateWorkersCounter() {
		return personRepository.incrementEWorkersCounter();
	}

	public Person assignManager(ObjectId managerId, ObjectId employeeId) {
		return personRepository.assignManager(managerId, employeeId);
	}

	public boolean isIdExist(ObjectId id) {
		return personRepository.isIdExist(id);
	}

	public Employee AssignTask(ObjectId managerId, ObjectId employeeId, Task task) {
		return personRepository.AssignTask(managerId, employeeId , task);
	}

	public UpdateResult updateTasksCounter() {
		return personRepository.updateTasksCounter();
	}

	public UpdateResult updateSTD() {
		return personRepository.updateSTD();
	}

	public List<Employee> overloadedEmployees() {
		return personRepository.overloadedEmployees();
	}

	public List<Task> getTasks(ObjectId employeeId) {
		return personRepository.getTasks(employeeId);
	}

	public Manager submitReport(ObjectId employeeId, ObjectId managerId, String reportText, Date reportDate) {
		return personRepository.submitReport(employeeId, managerId, reportText, reportDate);
	}

	public List<Report> getReports(ObjectId managerId) {
		return personRepository.getReports(managerId);
	}
	
}
