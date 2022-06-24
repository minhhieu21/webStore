package edu.poly.model;

import java.io.Serializable;
import java.util.Date;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto implements Serializable {
	private int orderId;
	
	private Date orderDate;
	
	private int customerId;
	
	private float amount;
	
	private short status;
}
