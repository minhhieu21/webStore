package edu.poly.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Account;
import edu.poly.domain.Customer;
import edu.poly.model.AdminLoginDto;
import edu.poly.model.CustomerDto;
import edu.poly.model.SiteLoginDto;
import edu.poly.service.AccountService;
import edu.poly.service.CustomerService;

@Controller
public class SiteLoginController {
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private HttpSession session;
	
	//site login
	@GetMapping("slogin")
	public String login (ModelMap model) {
		model.addAttribute("customer", new SiteLoginDto());
		
		return "/site/customers/login";
	}
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("customer", new CustomerDto());
		return "site/customers/registration";
	}
	
	@PostMapping("register")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("customer") 
	CustomerDto dto,BindingResult result){
		
		
//		nếu có lỗi -> nạp lại trang registration
		if (result.hasErrors()) {
			return new ModelAndView("site/customers/registration");
		}
		
		//Tạo ra đối tượng entity
		Customer entity = new Customer();
	
		
		dto.setRegisteredDate(new Date());
		
		//Copy dữ liệu từ đối tượng dto -> entity
		BeanUtils.copyProperties(dto, entity);
		
		//Lưu thông tin entity vào CSDL
		customerService.save(entity);
		
		
		//Hiển thị thông báo
		model.addAttribute("message", "Sign up successfully !");
		
		return new ModelAndView("redirect:/slogin" ,model);
	}

	
	@PostMapping("slogin")
	public ModelAndView login (ModelMap model,
			@Valid @ModelAttribute("customer") 
	SiteLoginDto dto, BindingResult result) {
		
		//nếu có lỗi
//		->trả về view login
		if (result.hasErrors()) {
			return new ModelAndView("/site/customers/login", model);
		}
		
		//gọi phương thức login -> để về đối tượng account
		Customer customer = customerService.login(dto.getEmail(), dto.getPassword());
		
		
		//nếu kh tìm thấy account
		if (customer == null) {
			
			//hiển thị thông báo
			model.addAttribute("message", "Invalid email or password");
			
			//trả về view login
			return new ModelAndView("/site/customers/login", model);
		}
		
		
		//thiết lập thuộc tính username
		// -> người dùng đã đăng nhập vào hệ thống
		
		session.setAttribute("email", customer.getEmail());
		
		
		//
		Object ruri = session.getAttribute("redirect-uri");
		
		//kiểm tra nếu tồn tại redirect uri
		if (ruri != null) {
			
			session.removeAttribute("redirect-uri");
			
			return new ModelAndView("redirect:" + ruri);
		}
		
		//forward đến
		return new ModelAndView("redirect:site/home", model);
		
	}
}
