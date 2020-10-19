package com.bpresice.validator;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.bpresice.service.PersonService;

@Service
public class Validator {

	private Validator service;

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
}
