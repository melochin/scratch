package spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import scratch.model.entity.User;
import scratch.model.view.UserForm;
import scratch.test.ContextTest;

public class ConvertTest extends ContextTest{

	@Autowired
	private ConversionService conversionService;
	
	@Test
	public void convertUserFormToUser() {
		// 准备表单类
		UserForm userForm = new UserForm();
		userForm.setUserId(new Long(1));
		userForm.setOldPassword("111");
		userForm.setNewPassword("222");
		//  转换
		User user = conversionService.convert(userForm, User.class);
		System.out.println(user);
	}
	
	
}
