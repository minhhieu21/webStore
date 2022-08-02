package edu.poly.controller.site;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Account;
import edu.poly.domain.Category;
import edu.poly.domain.Product;
import edu.poly.global.GlobalData;
import edu.poly.model.AccountDto;
import edu.poly.model.CategoryDto;
import edu.poly.model.ProductDto;
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
	
	@GetMapping("viewproduct/{productId}")
	public ModelAndView viewProduct(ModelMap model, @PathVariable("productId") Long productId) {
		
		//tìm kiếm id Product
		Optional<Product> opt = productService.findById(productId);
		
		//tạo ra đối tượng
		ProductDto dto = new ProductDto();
		
		
		//kiểm tra nếu tồn tại 
		if (opt.isPresent()) {
			
			//lấy thông tin Product
			Product entity = opt.get();
			
			//copy thông tin sang dto
			BeanUtils.copyProperties(entity, dto);
			
			
			model.addAttribute("product", dto);
			
			return new ModelAndView("site/home/viewProduct" ,model);
		}
		return new ModelAndView("redirect:/site/home", model);
		
	}
}
