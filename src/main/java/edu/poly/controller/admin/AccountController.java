package edu.poly.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Account;
import edu.poly.domain.Category;
import edu.poly.model.AccountDto;
import edu.poly.model.CategoryDto;
import edu.poly.service.AccountService;
import edu.poly.service.CategoryService;


@Controller
@RequestMapping("admin/accounts")
public class AccountController {
		
	@Autowired
	AccountService accountService;
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("account", new AccountDto ());
		
		
		
		return "admin/accounts/addOrEdit";
	}
	
	@GetMapping("edit/{username}")
	public ModelAndView edit(ModelMap model, @PathVariable("username") String username) {
		
		//tìm kiếm id category
		Optional<Account> opt = accountService.findById(username);
		
		//tạo ra đối tượng
		AccountDto dto = new AccountDto();
		
		
		//kiểm tra nếu tồn tại 
		if (opt.isPresent()) {
			
			//lấy thông tin category
			Account entity = opt.get();
			
			//copy thông tin sang dto
			BeanUtils.copyProperties(entity, dto);
			
			dto.setIsEdit(true);
			
			dto.setPassword("");
			
			model.addAttribute("account", dto);
			
			return new ModelAndView("admin/accounts/addOrEdit" ,model);
		}
		
		model.addAttribute("message","Account is not existed");
		
		return new ModelAndView("redirect:/admin/categories", model);
		
		 
	}
	
	@GetMapping("delete/{username}")
	public ModelAndView delete(ModelMap model,
			@PathVariable("username")String username) {
		
		accountService.deleteById(username);
		
		model.addAttribute("message", "Accounts is deleted !");
		
		return new ModelAndView("forward:/admin/accounts/", model);
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("account") AccountDto dto,BindingResult result) {
		
		//nếu có lỗi -> nạp lại trang addOrEdit
		if (result.hasErrors()) {
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		
		//Tạo ra đối tượng entity
		Account entity = new Account();
		
		//Copy dữ liệu từ đối tượng dto -> entity
		BeanUtils.copyProperties(dto, entity);
		
		//Lưu thông tin entity vào CSDL
		accountService.save(entity);
		
		
		//Hiển thị thông báo
		model.addAttribute("message", "The Account is saved !");
		
		return new ModelAndView("forward:/admin/accounts" ,model);
	}
	
	@RequestMapping("")
	public String list(ModelMap model) {
		
		List<Account> list =  accountService.findAll();
		
		model.addAttribute("accounts", list);
		
		return "admin/accounts/list";
	}
	
//	@GetMapping("search")
//	public String search(ModelMap model, //tìm kiếm thông tin theo tên
//			@RequestParam(name="name", required = false)String name) {
//		
//		List<Category> list = null;
//		
//		//kiểm tra nếu điền vào 
//		if (StringUtils.hasText(name)) {
//			list = categoryService.findByNameContaining(name);
//		}else {
//			list = categoryService.findAll();
//		}
//		
//		model.addAttribute("categories", list);
//		
//		return "admin/categories/search";
//	}
//	
//	
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
//		return "admin/categories/searchpaginated";
//	}
}
