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
	
//	@ModelAttribute("categories")
//	public List<CategoryDto> getCategories(){
//		return categoryService.findAll().stream().map(item->{
//			CategoryDto dto = new CategoryDto();
//			BeanUtils.copyProperties(item, dto);
//			return dto;
//		}).toList();
//	}
	
	@GetMapping("add")
	public String add(Model model) {
		
		ProductDto dto = new ProductDto();
		dto.setIsEdit(false);
		model.addAttribute("product", dto);
		
		return "admin/products/addOrEdit";
	}
	
	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
		
		//tìm kiếm id category
		Optional<Product> opt = productService.findById(productId);
		
		//tạo ra đối tượng
		ProductDto dto = new ProductDto();
		
		
		//kiểm tra nếu tồn tại 
		if (opt.isPresent()) {
			
			//lấy thông tin category
			Product entity = opt.get();
			
			//copy thông tin sang dto
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
		
		//tìm kiếm thông tin sản phẩm
		Optional<Product> opt = productService.findById(productId);
		
		//kiểm tra có hình không
		if (opt.isPresent()) {
			if (!StringUtils.isEmpty(opt.get().getImage())) {
				//tìm thấy sẽ xóa hình đại diện
				storageService.delete(opt.get().getImage());
			}
			//xóa sản phẩm
			productService.delete(opt.get());

			model.addAttribute("message", "Product is deleted !");

			
			//không tìm thấy sản phẩm
		}else {
			model.addAttribute("message", "Product is not found !");
		}
		
		return new ModelAndView("forward:/admin/products", model);
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("product") ProductDto dto,BindingResult result) {
		
		//nếu có lỗi -> nạp lại trang addOrEdit
		if (result.hasErrors()) {
			return new ModelAndView("admin/products/addOrEdit");
		}
		
		//Tạo ra đối tượng entity
		Product entity = new Product();
		
		//Copy dữ liệu từ đối tượng dto -> entity
		BeanUtils.copyProperties(dto, entity);
		
		Category category = new Category();
		category.setCategoryId(dto.getCategoryId());
		entity.setCategory(category);
		
		//lưu nội dung hình
		if (!dto.getImageFile().isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			
			entity.setImage(storageService.getStorageFilename(dto.getImageFile(), uuString));
			storageService.store(dto.getImageFile(), entity.getImage());
		}
		
		//Lưu thông tin entity vào CSDL
		productService.save(entity);
		
		//Hiển thị thông báo
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
	public String search(ModelMap model, //tìm kiếm thông tin theo tên
			@RequestParam(name="name", required = false)String name) {
		
		List<Product> list = null;
		
		//kiểm tra nếu điền vào 
		if (StringUtils.hasText(name)) {
			list = productService.findByNameContaining(name);
		}else {
			list = productService.findAll();
		}
		
		model.addAttribute("products", list);
		
		return "admin/products/search";
	}
	
	
//	@GetMapping("searchpaginated")
//	public String search(ModelMap model, //tìm kiếm thông tin theo tên
//			@RequestParam(name="name", required = false)String name,
//			@RequestParam("page") Optional<Integer> page,
//			@RequestParam("size") Optional<Integer> size){
//		
//		int currentPage = page.orElse(1);
//		int pageSize = size.orElse(5);
//		
//		//tạo ra đối tượng pageble
//		//Trang hiện tại, kích thước, sắp xếp theo tên?
//		Pageable pageble = PageRequest.of(currentPage-1, pageSize, Sort.by("name"));
//		
//		//
//		Page<Category> resultPage = null;
//		
//		
//		//kiểm tra nếu điền vào 
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
