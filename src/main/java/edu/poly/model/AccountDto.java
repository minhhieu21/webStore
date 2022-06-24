package edu.poly.model;


import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto implements Serializable{
	
	@NotEmpty
	@Length(min=4)
	private String username;
	
	
	@NotEmpty
	@Length(min=3)
	private String password;
	
	private Boolean isEdit = false;
}
