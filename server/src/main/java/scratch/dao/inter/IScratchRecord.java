package scratch.dao.inter;

import java.util.List;

import scratch.model.entity.ScratchRecord;

public interface IScratchRecord {

	List<ScratchRecord> list();

	void add(ScratchRecord record);
	
}
