package edu.poly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardItemDto {
	private int productId;
	private String name;
	private int quatity;
	private float unitPrice;
}	
