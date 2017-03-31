package scratch.bilibili.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import scratch.bilibili.model.Video;
import scratch.dao.BasicDao;
import scratch.service.Page;

@Repository
public class VideoDao extends BasicDao<Video>{

	private static final String QUERY_COUNT_TYPE = "select type.code,count(*) from Video group by type.code";
	
	public static final String ORDER_DATE = "createDate";
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<Long, Long> listCountByType() {
		Map<Long, Long> map = new HashMap<Long, Long>();
		List<Map> list = getCurrentSession().createQuery(QUERY_COUNT_TYPE)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
		for(Map m:list) {
			map.put((Long)m.get("0"), (Long)m.get("1"));
		}
		return map;
	}
	
	/**
	 * 功能：检索视频
	 * 
	 * 说明：如果type是null，则查询所有类型；
	 * 如果order是empty或者null，按照数据库默认排序
	 * 如果page是null，将不分页查询
	 * 
	 * @param keyword	搜索的关键字
	 * @param type		视频类型 
	 * @param order		排序
	 * @param page		页数
	 * @return
	 */
	public List<Video> list(String keyword, Long type, String order, Page page) {
		Criteria c = createCriteria(Video.class);
		if(keyword != null && !keyword.isEmpty()) {
			c.add(Restrictions.like("title", keyword, MatchMode.ANYWHERE));
		}
		if(type != null) {
			c.add(Restrictions.eq("type.code", type));
		}
		if(order != null) {
			switch (order) {
				case ORDER_DATE:
					c.addOrder(Order.desc(ORDER_DATE));
					break;
				default:
					break;
			}	
		}
		return listByCriteria(c, page);
	}
	
}
