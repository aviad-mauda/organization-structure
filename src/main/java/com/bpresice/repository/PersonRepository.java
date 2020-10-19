package com.bpresice.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.bpresice.entities.Counters;
import com.bpresice.entities.Employee;
import com.bpresice.entities.Manager;
import com.bpresice.entities.Person;
import com.bpresice.entities.Position;
import com.bpresice.entities.Report;
import com.bpresice.entities.Task;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

@Repository
public class PersonRepository {

	@Autowired 
	MongoTemplate mongoTemplate;
	
	public <T extends Person> T save(T document) {
		return mongoTemplate.save(document);
	}

	public UpdateResult incrementEWorkersCounter() {
		Query query = new Query();
		query.addCriteria(Criteria.where("$where").is("function() {\n" + 
				"   return (this.workers > -1)\n" + 
				"}"));
		Update update = new Update();
		update.inc("workers", 1);
		return mongoTemplate.updateFirst(query, update, Counters.class);
	}

	public Employee assignManager(ObjectId managerId, ObjectId employeeId) {
		Query query = new Query(Criteria.where("_id").is(employeeId));
		Update update = new Update();
		update.set("managerId", managerId);
		Employee res = mongoTemplate.findAndModify(query, update, Employee.class);
		return res;
	}
	
	public boolean isIdExist(ObjectId id) {
		return null !=  mongoTemplate.findById(id, Employee.class);
	}

	public Employee AssignTask(ObjectId managerId, ObjectId employeeId, Task task) {
		Employee employee = mongoTemplate.findById(employeeId, Employee.class);
		Manager manager = mongoTemplate.findById(managerId, Manager.class);
		if(manager.getPosition().name().equals(Position.MANAGER.name()) && employee.getManagerId() != null && employee.getManagerId().toHexString().equals(managerId.toHexString())) {
			employee.getTasks().add(task);
			Query query = new Query(Criteria.where("_id").is(employeeId));
			Update update = new Update();
			update.set("tasks", employee.getTasks());
			update.inc("taskCounter", 1);
			return mongoTemplate.findAndModify(query, update, Employee.class);
		}
		return null;
	}

	public UpdateResult updateTasksCounter() {
		Query query = new Query();
		query.addCriteria(Criteria.where("$where").is("function() {\n" + 
				"   return (this.workers > -1)\n" + 
				"}"));
		Update update = new Update();
		update.inc("tasks", 1);
		return mongoTemplate.updateFirst(query, update, Counters.class);
	}

	public UpdateResult updateSTD() {
//		List<Counters> employeesAndTasks= employeesAndTasks();
//		Integer workers = employeesAndTasks.get(0).getWorkers();
//		Integer tasks = employeesAndTasks.get(0).getTasks();
		List<Employee> employees = getTaskFromDocs();
		double std = calculateStd(employees);
		
		Query query = new Query();
		query.addCriteria(Criteria.where("$where").is("function() {\n" + 
				"   return (this.workers > -1)\n" + 
				"}"));
		Update update = new Update();
		update.set("std", std);
		UpdateResult res = mongoTemplate.updateFirst(query, update, Counters.class);
		return res;
	}

	private double calculateStd(List<Employee> employees) {
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
		for (Employee employee : employees) {
		    descriptiveStatistics.addValue(employee.getTaskCounter());
		    System.out.println(employee.getTaskCounter());
		}
		return descriptiveStatistics.getStandardDeviation();
	}
	
	private List<Employee> getTaskFromDocs() {
		Query query = new Query();
		query.fields().include("taskCounter");
		return mongoTemplate.find(query, Employee.class);
	}

	public List<Employee> overloadedEmployees() {
		Query query = new Query();
		query.fields().include("taskCounter").include("managerId");
		List<Employee> employees = mongoTemplate.find(query, Employee.class);
		
		List<Counters> employeesAndTasks= employeesAndTasks();
		Counters counters = employeesAndTasks.get(0);
		Integer workers = counters.getWorkers();
		Integer tasks = counters.getTasks();
		Double average = (double) (tasks/workers);
		Double std = counters.getStd();
		
		List<Employee> res = new ArrayList(); 
		for(Employee employee : employees) {
			if(employee.getTaskCounter() >= (average + std)) { 
				res.add(employee);
			}
		}
		return res;
		
	}

	private List<Counters> employeesAndTasks() {
		Query query = new Query();
		query.addCriteria(Criteria.where("$where").is("function() {\n" + 
				"   return (this.workers > -1)\n" + 
				"}"));
		return mongoTemplate.find(query, Counters.class);
	}

	public List<Task> getTasks(ObjectId employeeId) {
		Query query = new Query();
		query.fields().include("tasks");
		Employee res = mongoTemplate.findById(employeeId, Employee.class);
		return res.getTasks();
	}

	public Manager submitReport(ObjectId employeeId, ObjectId managerId, String reportText, Date reportDate) {
		Query query = new Query();
		query.fields().include("tasks");
		Manager manager = mongoTemplate.findById(managerId, Manager.class);
		Employee employee = mongoTemplate.findById(employeeId, Employee.class);
		if(manager.getPosition().name().equals(Position.MANAGER.name()) && employee.getManagerId() != null && employee.getManagerId().toHexString().equals(managerId.toHexString())) {
			List<Report> reports = manager.getReports();
			if (reports == null) reports = new ArrayList<Report>();
			setReport(employeeId, reportText, reportDate, reports);
			
			Query qry = new Query(Criteria.where("_id").is(managerId));
			Update update = new Update();
			update.set("reports", reports);
			FindAndModifyOptions options = new FindAndModifyOptions();
			options.returnNew(true);
			return mongoTemplate.findAndModify(qry, update, options, Manager.class);
		}
		return null;
	}

	private void setReport(ObjectId employeeId, String reportText, Date reportDate, List<Report> reports) {
		Report report = new Report();
		report.setEmployeeId(employeeId.toHexString());
		report.setReportDate(reportDate);
		report.setReportText(reportText);
		reports.add(report);
	}
	

}
