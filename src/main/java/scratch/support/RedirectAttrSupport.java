package scratch.support;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class RedirectAttrSupport {

	public static void setError(RedirectAttributes ra, BindingResult result) {
		if(!result.hasErrors()) {
			return;
		}
		StringBuilder error = new StringBuilder();
		List<ObjectError> list = result.getAllErrors();
		for(ObjectError e : list) {
			error.append(e.getDefaultMessage());
		}
		ra.addFlashAttribute("error", error);
	}
}
