package com.bpresice.validator;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.bpresice.entities.Task;
import com.bpresice.service.PersonService;

@Service
public class Validator {

	public boolean isIdExist(PersonService service, ObjectId id) {
		return service.isIdExist(id);
	}

	public boolean isIdValid(String id) {
		ObjectId employeeId = null;
		try { 
			employeeId = new ObjectId(id);
		} catch (IllegalArgumentException e) { 
			 return false;
		}
		return true;
	}

	public boolean isDateLater(Date date, Date later) {
		if(date.getTime() >= later.getTime()) {
			return false;
		}
		return true;
	}
}
