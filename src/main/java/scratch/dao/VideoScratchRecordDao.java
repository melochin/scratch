package scratch.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.VideoScratchRecord;

@Repository
@Transactional
public class VideoScratchRecordDao extends BasicDao<VideoScratchRecord> {

	private static final String QUERY_MAX = "from VideoScratchRecord order by id desc limit 1,1";
	
	public VideoScratchRecord getRecent() {
		Query query = getCurrentSession().createQuery(QUERY_MAX);
		query.setFirstResult(0);
		query.setMaxResults(1);
		return (VideoScratchRecord) query.uniqueResult();
	}
}
