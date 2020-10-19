package com.bpresice.rest;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bpresice.entities.Counters;
import com.bpresice.entities.Employee;
import com.bpresice.entities.Manager;
import com.bpresice.entities.Person;
import com.bpresice.entities.Position;
import com.bpresice.entities.Report;
import com.bpresice.entities.Task;
import com.bpresice.rest.entities.AssignRequest;
import com.bpresice.rest.entities.AssignTask;
import com.bpresice.rest.entities.GetReports;
import com.bpresice.rest.entities.GetTasks;
import com.bpresice.rest.entities.SubmitReport;
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
	public ResponseEntity<?> saveEmployee(@RequestBody Person person) {
		Employee employee = new Employee();
		setWorker(person,employee);
		employee.setPosition(Position.EMPLOYEE);
		Employee saved = service.save(employee);
		if(saved == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		updateWorkersCounter();
		updateSTD();
		return new ResponseEntity<>(HttpStatus.OK);
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
	public ResponseEntity<?> saveManager(@RequestBody Person person) {
		Manager manager = new Manager();
		setWorker(person, manager);
		manager.setPosition(Position.MANAGER);
		
		Manager saved = service.save(manager);
		if(saved == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		updateWorkersCounter();
		updateSTD();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/assignManager")
	@ResponseBody
	public ResponseEntity<?> assignManager(@RequestBody AssignRequest ids) {
		
		if(!validator.isIdValid(ids.getManagerId()) || !validator.isIdValid(ids.getEmployeeId())) {
			return new ResponseEntity<>("id is not valid",HttpStatus.BAD_REQUEST);
		}
		ObjectId managerId = new ObjectId(ids.getManagerId());
		ObjectId employeeId = new ObjectId(ids.getEmployeeId());

		if(validator.isIdExist(service, managerId) && validator.isIdExist(service ,employeeId)) {
			service.assignManager(managerId, employeeId);
			return new ResponseEntity<>(HttpStatus.OK);
		} else { 
			return new ResponseEntity<>("employee was not saved. id is not exist",HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/assignTask")
	@ResponseBody
	public ResponseEntity<?> assignTask(@RequestBody AssignTask assignTask) {

		if(!validator.isIdValid(assignTask.getManagerId()) || !validator.isIdValid(assignTask.getEmployeeId())) {
			return new ResponseEntity<>("id is not valid",HttpStatus.BAD_REQUEST);
		}
		ObjectId managerId = new ObjectId(assignTask.getManagerId());
		ObjectId employeeId = new ObjectId(assignTask.getEmployeeId());
		
		if(validator.isIdExist(service, managerId) && validator.isIdExist(service ,employeeId)) {
			Employee res = service.AssignTask(managerId, employeeId, assignTask.getTask());
			if(res == null) {
				return new ResponseEntity<>("task was not saved - manager not assigned to this employee or the position is employee",HttpStatus.BAD_REQUEST);
			}
			updateTasksCounter();
			updateSTD();
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>("task was not saved. id is not exist",HttpStatus.BAD_REQUEST);
		}
	}
	
	private UpdateResult updateSTD() {
		return service.updateSTD();
		
	}

	private UpdateResult updateTasksCounter() {
		return service.updateTasksCounter();
	}

	@GetMapping("/overloadedEmployees")
	public ResponseEntity<?> overloadedEmployees(){
		return new ResponseEntity<>(service.overloadedEmployees(), HttpStatus.OK);
	}
	
	@PostMapping("/getTasks")
	public ResponseEntity<?> getTasks(@RequestBody GetTasks id){
		
		if(!validator.isIdValid(id.getEmployeeId())) {
			return new ResponseEntity<>("id is not valid",HttpStatus.BAD_REQUEST);
		}
		ObjectId employeeId = new ObjectId(id.getEmployeeId());
		
		return new ResponseEntity<>(service.getTasks(employeeId), HttpStatus.OK);
	}

//	private boolean isIdValid(String id) {
//		ObjectId employeeId = null;
//		try { 
//			employeeId = new ObjectId(id);
//		} catch (IllegalArgumentException e) { 
//			 return false;
//		}
//		return true;
//	}
	
	@PostMapping("/submitReport")
	public ResponseEntity<?> submitReport(@RequestBody SubmitReport report){
		
		if(!validator.isIdValid(report.getReport().getEmployeeId()) || !validator.isIdValid(report.getManagerId())) {
			return new ResponseEntity<>("id is not valid",HttpStatus.BAD_REQUEST);
		}
		ObjectId managerId = new ObjectId(report.getReport().getEmployeeId());
		ObjectId employeeId = new ObjectId(report.getManagerId());
		
		if(validator.isIdExist(service, managerId) && validator.isIdExist(service ,employeeId)) {
			Report reportProperties = report.getReport();
			Manager res = service.submitReport(employeeId, managerId, reportProperties.getReportText(), reportProperties.getReportDate());
			if(res == null) {
				return new ResponseEntity<>("task was not saved - manager not assigned to this employee or the position is employee",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else { 
			return new ResponseEntity<>("id is not exist",HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/getReports")
	public ResponseEntity<?> getReports(@RequestBody GetReports reportManager){
		if(!validator.isIdValid(reportManager.getManagerId())) {
			return new ResponseEntity<>("id is not valid",HttpStatus.BAD_REQUEST);
		}
		
		ObjectId managerId = new ObjectId(reportManager.getManagerId());
		
		if(validator.isIdExist(service, managerId)) {
			return new ResponseEntity<>(service.getReports(managerId), HttpStatus.OK);
		} else { 
			return new ResponseEntity<>("id is not exist",HttpStatus.NOT_FOUND);
		}
	}
}
