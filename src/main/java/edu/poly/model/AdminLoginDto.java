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
public class AdminLoginDto implements Serializable{
	
	@NotEmpty
	private String username;
	
	
	@NotEmpty
	private String password;
	
	private Boolean rememberMe  = false;
}
