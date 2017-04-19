package scratch.support;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ModelSupport {

	private static final String ERROR = "error";
	
	private static final String SUCCESS = "success";
	
	public static void setSuccess(Model model, String message) {
		model.addAttribute(SUCCESS, message);
	}
	
	public static void setSuccess(RedirectAttributes ra, String message) {
		ra.addFlashAttribute(SUCCESS, message);
	}
	
	public static void setError(Model model, String message) {
		model.addAttribute(ERROR, message);
	}
	
	public static void setError(RedirectAttributes ra, String message) {
		ra.addFlashAttribute(ERROR, message);
	}
	
	public static void setError(RedirectAttributes ra, BindingResult result) {
		if(!result.hasErrors()) {
			return;
		}
		StringBuilder error = new StringBuilder();
		List<ObjectError> list = result.getAllErrors();
		for(ObjectError e : list) {
			error.append(e.getDefaultMessage());
		}
		setError(ra, error.toString());
	}
	
}
