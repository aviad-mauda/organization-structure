package com.bpresice.entities;

import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Person implements IPerson{

//	@Id
//	private UUID id;
	private String firstName;
	private String lastName;
	private Position position;
	private ObjectId managerId;
	private List<Task> tasks;
	private Integer taskCounter;
	
	
	public Integer getTaskCounter() {
		return taskCounter;
	}

	public void setTaskCounter(Integer taskCounter) {
		this.taskCounter = taskCounter;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

//	public UUID getId() {
//		return id;
//	}
//
//	public void setId(UUID id) {
//		this.id = id;
//	}

	@Override
	public void savePerson(Person person) {
	}

	public ObjectId getManagerId() {
		return managerId;
	}

	public void setManagerId(ObjectId managerId) {
		this.managerId = managerId;
	}
	
}
