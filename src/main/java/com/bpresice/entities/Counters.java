package com.bpresice.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "counters")
public class Counters {
	Integer workers;
	Integer tasks;
	Double std;
	
	public Double getStd() {
		return std;
	}
	public void setStd(Double std) {
		this.std = std;
	}
	public Integer getWorkers() {
		return workers;
	}
	public void setWorkers(Integer workers) {
		this.workers = workers;
	}
	public Integer getTasks() {
		return tasks;
	}
	public void setTasks(Integer tasks) {
		this.tasks = tasks;
	}
}
