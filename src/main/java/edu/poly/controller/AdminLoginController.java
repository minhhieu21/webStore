package edu.poly.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.domain.Account;
import edu.poly.model.AdminLoginDto;
import edu.poly.service.AccountService;

@Controller
public class AdminLoginController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private HttpSession session;
	
	//admin login
	@GetMapping("alogin")
	public String login (ModelMap model) {
		model.addAttribute("account", new AdminLoginDto());
		
		return "/admin/accounts/login";
	}
	
	@PostMapping("alogin")
	public ModelAndView login (ModelMap model,
			@Valid @ModelAttribute("account") 
	AdminLoginDto dto, BindingResult result) {
		
		//nếu có lỗi
//		->trả về view login
		if (result.hasErrors()) {
			return new ModelAndView("/admin/accounts/login", model);
		}
		
		//gọi phương thức login -> để về đối tượng account
		Account account = accountService.login(dto.getUsername(), dto.getPassword());
		
		
		//nếu kh tìm thấy account
		if (account == null) {
			
			//hiển thị thông báo
			model.addAttribute("message", "Invalid username or password");
			
			//trả về view login
			return new ModelAndView("/admin/accounts/login", model);
		}
		
		
		//thiết lập thuộc tính username
		// -> người dùng đã đăng nhập vào hệ thống
		
		session.setAttribute("username", account.getUsername());
		
		
		//
		Object ruri = session.getAttribute("redirect-uri");
		
		//kiểm tra nếu tồn tại redirect uri
		if (ruri != null) {
			
			session.removeAttribute("redirect-uri");
			
			return new ModelAndView("redirect:" + ruri);
		}
		
		
		
		
		//forward đến
		return new ModelAndView("forward:/admin/categories", model);
		
	}
}
