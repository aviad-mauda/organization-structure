package com.bpresice.rest;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bpresice.entities.AssignRequest;
import com.bpresice.entities.AssignTask;
import com.bpresice.entities.Counters;
import com.bpresice.entities.Employee;
import com.bpresice.entities.Manager;
import com.bpresice.entities.Person;
import com.bpresice.entities.Position;
import com.bpresice.entities.Task;
import com.bpresice.service.PersonService;
import com.bpresice.validator.Validator;
import com.mongodb.client.result.UpdateResult;

@RequestMapping("/")
@RestController
public class OrganizationRestController {
	
	@Autowired
	Validator validator;
	@Autowired
	PersonService service;
	
	@PostMapping("/saveEmployee")
	@ResponseBody
	public String saveEmployee(@RequestBody Person person) {
		Employee employee = new Employee();
		setWorker(person,employee);
		employee.setPosition(Position.EMPLOYEE);
		Employee saved = service.save(employee);
		if(saved == null) {
			return "object with name " + saved.getFirstName() + " was not saved.";
		}
		updateWorkersCounter();
		updateSTD();
		return "object with name " + saved.getFirstName()  + " was saved";
	}
	
	private void updateWorkersCounter() {
		service.updateWorkersCounter();
	}

	private <T extends Person> void setWorker(Person person , T worker) {
		worker.setFirstName(person.getFirstName());
		worker.setLastName(person.getLastName());
		worker.setTasks(new ArrayList<Task>());
		worker.setTaskCounter(0);
	}

	@PostMapping("/saveManager")
	@ResponseBody
	public String saveManager(@RequestBody Person person) {
		Manager manager = new Manager();
		setWorker(person, manager);
		manager.setPosition(Position.MANAGER);
		
		Manager saved = service.save(manager);
		if(saved == null) {
			return "object with name " + saved.getFirstName() + " was not saved.";
		}
		updateWorkersCounter();
		updateSTD();
		return "object with name " + saved.getFirstName()  + " was saved";
	}
	
	@PostMapping("/assignManager")
	@ResponseBody
	public String assignManager(@RequestBody AssignRequest ids) {
		ObjectId managerId = null;
		ObjectId employeeId = null;

		try { 
			managerId = new ObjectId(ids.getManagerId());
			employeeId = new ObjectId(ids.getEmployeeId());
		} catch (IllegalArgumentException e) { 
			return "employee was not saved: " + e; 
		}
		if(validator.isIdExist(service, managerId) && validator.isIdExist(service ,employeeId)) {
			service.assignManager(managerId, employeeId);
			return "employee was saved" ;
		} else { 
			return "employee was not saved. id is not exist";
		}
	}

	@PostMapping("/assignTask")
	@ResponseBody
	public String assignTask(@RequestBody AssignTask assignTask) {
		ObjectId managerId = null;
		ObjectId employeeId = null;

		try { 
			managerId = new ObjectId(assignTask.getManagerId());
			employeeId = new ObjectId(assignTask.getEmployeeId());
		} catch (IllegalArgumentException e) { 
			return "employee was not saved: " + e; 
		}
		if(validator.isIdExist(service, managerId) && validator.isIdExist(service ,employeeId)) {
			Employee res = service.AssignTask(managerId, employeeId, assignTask.getTask());
			if(res == null) { 
				return "task was not saved - manager not assigned to this employee or the position is employee";
			}
			updateTasksCounter();
			updateSTD();
			return "task was saved" ;
		} else { 
			return "task was not saved. id is not exist";
		}
	}
	
	private UpdateResult updateSTD() {
		return service.updateSTD();
		
	}

	private UpdateResult updateTasksCounter() {
		return service.updateTasksCounter();
	}

	@GetMapping("/overloadedEmployees")
	public List<Employee> overloadedEmployees(){
		return service.overloadedEmployees();
		
	}
}