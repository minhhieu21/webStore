package edu.poly.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail implements Serializable{

	private int orderDetailId;
	
	private int orderId;
	
	private int productId;
	
	private int quatity;
	
	private float unitPrice;
}
