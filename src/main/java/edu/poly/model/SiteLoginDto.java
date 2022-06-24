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
public class SiteLoginDto implements Serializable{
	
	@NotEmpty
	private String email;
	
	
	@NotEmpty
	private String password;
	
	private Boolean rememberMe  = false;
}
