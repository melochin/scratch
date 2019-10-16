package scratch.service.anime;

import scratch.api.renren.RenrenImpl;
import scratch.service.reader.adpater.RenrenAdapter;
import scratch.service.reader.adpater.ScratchAdpater;

public class ScratchFactory {

	private static RenrenAdapter renrenAdapter;

	static {
		renrenAdapter = new RenrenAdapter(new RenrenImpl());
	}

	public static ScratchAdpater get(long id) {
		if (3 == id) {
			return renrenAdapter;
		}
		throw new RuntimeException("Can not create ScratchAdpater, Because of unknown hostId " + id);
	}

}
