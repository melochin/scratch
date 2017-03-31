package scratch.service.specific;

import java.util.List;

import com.rory.regex.RegexMatch;

public interface ScratchReader<T> {

	void read(String html, RegexMatch match, List<T> returnList);
	
}
