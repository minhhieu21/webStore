package edu.poly.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//
@Component
public class SiteAuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	private  HttpSession session;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		System.out.println("pre handle of request" +request.getRequestURI());
		
		//kiểm tra username có tồn tại trong session không
		// -> có tồn tại
		if (session.getAttribute("email") != null) {
			return true;
		}
		
		
		//thiết lập biến redirect-uri ở trong session
		session.setAttribute("redirect-uri", request.getRequestURI());
		
		//yêu cầu login
		response.sendRedirect("/slogin");
		
		//k tiếp tục thực hiện yêu cầu
		// -> phải login
		return false;
	}
	
}
