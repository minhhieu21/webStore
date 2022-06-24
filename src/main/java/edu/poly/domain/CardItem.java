package edu.poly.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardItem {
	private int productId;
	private String name;
	private int quatity;
	private float unitPrice;
}	
