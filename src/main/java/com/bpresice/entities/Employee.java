package com.bpresice.entities;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import com.bpresice.entities.Person;
@Data
@Document(collection = "employees")
public class Employee extends Person{

}
