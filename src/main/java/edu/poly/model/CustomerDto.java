package edu.poly.model;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CustomerDto implements Serializable {

	private Long customerId;
	
	private String name;
	
	@NotEmpty
	@Length(min=7)
	private String email;
	
	
	@NotEmpty
	@Length(min=3)
	private String password;
	
	private String phone;
	
	private Date registeredDate;

	private short status;
}
