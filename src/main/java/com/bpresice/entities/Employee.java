package com.bpresice.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document(collection = "employees")
public class Employee extends Person{

}
