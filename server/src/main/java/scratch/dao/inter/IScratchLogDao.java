package scratch.dao.inter;

import org.apache.ibatis.annotations.Param;
import scratch.model.entity.ScratchLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IScratchLogDao {

	void save(@Param("scratchLog") ScratchLog scratchLog);

	List<Map<String, Object>> groupByType(Date date);
	// TODO 输出的结果有误
	List<ScratchLog> listByDate(Date date);
}
