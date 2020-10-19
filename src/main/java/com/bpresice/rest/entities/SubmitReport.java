package com.bpresice.rest.entities;

import com.bpresice.entities.Report;

public class SubmitReport {
	String managerId;
	Report report;

	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
}
