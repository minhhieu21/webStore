package edu.poly.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Category;
import edu.poly.domain.Product;
import edu.poly.model.CategoryDto;
import edu.poly.model.ProductDto;
import edu.poly.service.CategoryService;
import edu.poly.service.ProductService;
import edu.poly.service.StorageService;


@Controller
@RequestMapping("admin/products")
public class ProductController {
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@ModelAttribute("categories")
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@GetMapping("add")
	public String add(Model model) {
		
		ProductDto dto = new ProductDto();
		dto.setIsEdit(false);
		model.addAttribute("product", dto);
		
		return "admin/products/addOrEdit";
	}
	
	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
		
		//t??m ki???m id category
		Optional<Product> opt = productService.findById(productId);
		
		//t???o ra ?????i t?????ng
		ProductDto dto = new ProductDto();
		
		
		//ki???m tra n???u t???n t???i 
		if (opt.isPresent()) {
			
			//l???y th??ng tin category
			Product entity = opt.get();
			
			//copy th??ng tin sang dto
			BeanUtils.copyProperties(entity, dto);
			
			dto.setCategoryId(entity.getCategory().getCategoryId());
		 	dto.setIsEdit(true);
			
			model.addAttribute("product", dto);
			
			return new ModelAndView("admin/products/addOrEdit" ,model);
		}
		
		model.addAttribute("message","Product is not existed");
		
		return new ModelAndView("redirect:/admin/products", model);
		
		
	}
	
	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> saveFile(@PathVariable String filename){
		
		Resource file = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, 
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
		
	}
	
	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model,
			@PathVariable("productId")Long productId) throws IOException {
		
		//t??m ki???m th??ng tin s???n ph???m
		Optional<Product> opt = productService.findById(productId);
		
		//ki???m tra c?? h??nh kh??ng
		if (opt.isPresent()) {
			if (!StringUtils.isEmpty(opt.get().getImage())) {
				//t??m th???y s??? x??a h??nh ?????i di???n
				storageService.delete(opt.get().getImage());
			}
			//x??a s???n ph???m
			productService.delete(opt.get());

			model.addAttribute("message", "Product is deleted !");

			
			//kh??ng t??m th???y s???n ph???m
		}else {
			model.addAttribute("message", "Product is not found !");
		}
		
		return new ModelAndView("forward:/admin/products", model);
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("product") ProductDto dto,BindingResult result) {
		
		//n???u c?? l???i -> n???p l???i trang addOrEdit
		if (result.hasErrors()) {
			return new ModelAndView("admin/products/addOrEdit");
		}
		
		//T???o ra ?????i t?????ng entity
		Product entity = new Product();
		
		//Copy d??? li???u t??? ?????i t?????ng dto -> entity
		BeanUtils.copyProperties(dto, entity);
		
		Category category = new Category();
		category.setCategoryId(dto.getCategoryId());
		entity.setCategory(category);
		
		//l??u n???i dung h??nh
		if (!dto.getImageFile().isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			
			entity.setImage(storageService.getStorageFilename(dto.getImageFile(), uuString));
			storageService.store(dto.getImageFile(), entity.getImage());
		}
		
		//L??u th??ng tin entity v??o CSDL
		productService.save(entity);
		
		//Hi???n th??? th??ng b??o
		model.addAttribute("message", "The Products is saved !");
		
		return new ModelAndView("forward:/admin/products" ,model);
	}
	
	@RequestMapping("")
	public String list(ModelMap model) {
		
		List<Product> list =  productService.findAll();
		
		model.addAttribute("products", list);
		
		return "admin/products/list";
	}
	
	@GetMapping("search")
	public String search(ModelMap model, //t??m ki???m th??ng tin theo t??n
			@RequestParam(name="name", required = false)String name) {
		
		List<Product> list = null;
		
		//ki???m tra n???u ??i???n v??o 
		if (StringUtils.hasText(name)) {
			list = productService.findByNameContaining(name);
		}else {
			list = productService.findAll();
		}
		
		model.addAttribute("products", list);
		
		return "admin/products/search";
	}
	
	
//	@GetMapping("searchpaginated")
//	public String search(ModelMap model, //t??m ki???m th??ng tin theo t??n
//			@RequestParam(name="name", required = false)String name,
//			@RequestParam("page") Optional<Integer> page,
//			@RequestParam("size") Optional<Integer> size){
//		
//		int currentPage = page.orElse(1);
//		int pageSize = size.orElse(5);
//		
//		//t???o ra ?????i t?????ng pageble
//		//Trang hi???n t???i, k??ch th?????c, s???p x???p theo t??n?
//		Pageable pageble = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
//		
//		//
//		Page<Category> resultPage = null;
//		
//		
//		//ki???m tra n???u ??i???n v??o 
//		if (StringUtils.hasText(name)) {
//			
//			resultPage = categoryService.findByNameContaining(name,pageble);
//			model.addAttribute("name", name);
//			
//		}else {
//			resultPage = categoryService.findAll(pageble);
//		}
//		
//		int totalPages = resultPage.getTotalPages();
//		
//		if (totalPages > 0) {
//			
//			int start = Math.max(1, currentPage-2);
//			int end = Math.min(currentPage + 2, totalPages);
//
//			if (totalPages > 5) {
//				if (end == totalPages) start = end -5; 
//				else if(start ==1) end = start + 5;	
//			}
//			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
//					.boxed()
//					.collect(Collectors.toList());
//			
//			model.addAttribute("pageNumbers", pageNumbers);
//		}
//		
//		model.addAttribute("categoryPage", resultPage);
//		
//		return "admin/products/searchpaginated";
//	}
}
