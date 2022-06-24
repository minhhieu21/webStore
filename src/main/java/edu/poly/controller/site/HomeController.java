package edu.poly.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.poly.domain.Product;
import edu.poly.model.AccountDto;
import edu.poly.service.ProductService;
import edu.poly.service.StorageService;

@Controller
@RequestMapping("site")
public class HomeController {
	
	@Autowired
	ProductService productService;
	
	@Autowired
	StorageService storageService;
//	
//	@RequestMapping("home")
//	public String add(Model model) {
////		model.addAttribute("account", new AccountDto ());
//		return "site/home/demo";
//	}
	
	@RequestMapping("home")
	public String list(ModelMap model) {
		
		List<Product> list =  productService.findAll();
		
		model.addAttribute("products", list);
		
		return "site/home/demo";
	}
	
	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> saveFile(@PathVariable String filename){
		
		Resource file = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, 
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
		
	}
}
