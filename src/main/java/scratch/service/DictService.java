package scratch.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scratch.dao.inter.IDictDao;
import scratch.model.RedisKey;
import scratch.model.entity.Dict;
import scratch.model.ohter.DictList;

@Service
public class DictService {
	
	@Autowired 
	private IDictDao dao;

	@Autowired
	private RedisService redisService;
	
	@Transactional
	public int save(Dict dict) {
		return dao.save(dict);
	}
	
	@Transactional
	public int update(Dict dict) {
		return dao.update(dict);
	}
	
	@Transactional
	public int delete(Dict dict) {
		return dao.delete(dict);
	}
	
	public List<Dict> findAllDictionaries() {
		return dao.findByParentCode("-1", false);
	}
	
	public Dict findByCodeAndParentCode(String code, String parentCode) {
		return dao.findByCodeAndParentCode(code, parentCode);
	}
	
	/**
	 * 直接从数据库中读取，读取的字典包含停止使用的项目
	 * 主要针对后台维护使用的
	 * @param parentCode
	 * @return
	 */
	public List<Dict> findByParentCode(String parentCode) {
		return dao.findByParentCode(parentCode, false);
	}
	
	/**
	 * 使用缓存
	 * @param parentCode
	 * @return
	 */
	public DictList findByType(String parentCode) {
		// redis连接的状态标志
		// 连接:数据将存储在缓存 	未连接:直接走DB
		boolean redisConnected = redisService.isConnected();
		DictList dicts = null;
		
		// 尝试从redis中读取字典数据
		if(redisConnected) {
			dicts = (DictList) redisService.hashGet(RedisKey.DIC, parentCode);
			if(dicts != null) return dicts;
		}

		// 直接从DB中读取字典数据
		dicts = dao.findByParentCode(parentCode, true);
		if(dicts == null || dicts.size() == 0) return null;

		// 将数据缓存在redis中
		if(redisConnected) {
			redisService.hashPut(RedisKey.DIC, parentCode, dicts);
		}
		
		return dicts;
	}

}

