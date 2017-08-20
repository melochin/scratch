package scratch.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageRowBounds;

import scratch.dao.inter.IAnimeDao;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.support.FileUtils;
import scratch.support.PageFactory;

@Service
public class AnimeService {

	/**
	 * @deprecated 将用图片服务代替
	 */
	private static final String UPLOAD_ANIME = "/upload/anime";
	
	private static final String SEARCH_HISTORY = "searchHistory";
	
	private RedisTemplate<String, String> redisTemplate;
	
	private IAnimeDao animeDao;
	
	@Autowired
	public AnimeService(IAnimeDao animeDao, RedisTemplate<String, String> redisTemplate) {
		this.animeDao = animeDao;
		this.redisTemplate = redisTemplate;
	}
	
	public List<Anime> list() {
		return animeDao.list();
	}
	
	public List<Anime> listByType(String type) {
		return animeDao.listByTypeLeftJointFocus(type);
	}
	
	public List<Anime> listByName(String name, Long userId) {
		if(userId != null) {
			String key = SEARCH_HISTORY + ":" + userId;
			SetOperations<String, String> ops = redisTemplate.opsForSet();
			if(ops.size(key) >= 5) {
				ops.pop(key);
			}
			ops.add(key, name);
		}
		return animeDao.listByNameLeftJoinFocus(name);
	}
	
	/** 查询最受关注的Anime **/
	public List<Anime> listMostFocused() {
		return animeDao.listMostFocused();
	}
	
	/** 查询最受关注的Anime
	 *	@param limit 限制显示个数
	 */ 
	public List<Anime> listMostFocused(int limit) {
		List<Anime> animes = listMostFocused();
		return animes.size() > limit ? animes.subList(0, limit) : animes;
	}
	
	/** 将最受关注的Anime，根据类型分组 **/
	public Map<String,List<Anime>> listMostFcousedGroupByType() {
		List<Anime> animes = listMostFocused();
		return animes.stream().collect(Collectors.groupingBy(Anime::getType));
	}
	
	/**
	 * 将最受关注的Anime，根据类型分组
	 * @param limit 限制每组显示个数
	 */
	public Map<String,List<Anime>> listMostFcousedGroupByType(int limit) {
		Map<String, List<Anime>> map = listMostFcousedGroupByType();
		// 遍历分组的animes，对数量进行限制调整
		map.entrySet().forEach(entry -> {
			List<Anime> animes = entry.getValue();
			animes = animes.size() > limit ? animes.subList(0, limit) : animes;
			entry.setValue(animes);
		});
		return map;
	}
	
	public Page<Anime> pageByType(String type, Integer page) {
		// type 空字串 当 null处理
		type = Optional.ofNullable(type)
				.filter(s -> !StringUtils.isEmpty(s))
				.orElse(null);
		PageRowBounds pageRowBounds = PageFactory.asList(page);
		return animeDao.pageByType(type, pageRowBounds);
	}
	
	public Anime getById(Long animeId) {
		return animeDao.getById(animeId);
	}
	
	@Transactional
	public void save(Anime anime) {
		animeDao.save(anime);
	}
	
	@Transactional
	public void update(Anime anime) {
		animeDao.update(anime);
	}
	
	@Transactional
	public void delete(Long id) {
		animeDao.delete(id);
	}
	
	/**
	 * 
	 * @param anime
	 * @param multipartFile
	 * @param realPath
	 * @return
	 * @throws Exception 
	 */
	public void saveWithPicFile(Anime anime, MultipartFile multipartFile, String realPath) 
			throws IllegalStateException, IOException  {
		File file = getFile(multipartFile, realPath);
		anime.setPic(file.getName());
		save(anime);
		multipartFile.transferTo(file);
	}
	
	public void updateWithFile(Anime anime, MultipartFile multipartFile, String realPath) 
			throws IllegalStateException, IOException  {
		//删除老的文件
		Anime originAnime = animeDao.getById(anime.getId());
		deleteFile(originAnime.getPic(), realPath);
		
		//更新文件名
		File file = getFile(multipartFile, realPath);
		anime.setPic(file.getName());
		update(anime);
		
		//保存文件
		multipartFile.transferTo(file);
	}

	public void deleteWithFile(Long animeId, String realPath) {
		Anime anime = animeDao.getById(animeId);
		deleteFile(anime.getPic(), realPath);
		animeDao.delete(animeId);
	}
	
	private File getFile(MultipartFile multipartFile, String realPath) {
		//获取表单上传文件的后缀名
		String originFilename = multipartFile.getOriginalFilename();
		String suffix = FileUtils.getSuffix(originFilename);
		//使用UUID作为文件名
		String filename = UUID.randomUUID().toString() + "." + suffix;
		return new File(FilenameUtils.concat(realPath + UPLOAD_ANIME, filename));
	}
	
	private void deleteFile(String filename, String realPath) {
		if(!StringUtils.isEmpty(filename)) {
			File file = new File(realPath + UPLOAD_ANIME, filename);
			file.delete();
		}
	}

	public Anime findByIdWithAlias(Long id) {
		return animeDao.getByIdWithAlias(id);
	}

	@Transactional
	public void saveAlias(AnimeAlias alias) {
		animeDao.saveAlias(alias);
	}

	@Transactional
	public void modifyAlias(AnimeAlias alias) {
		animeDao.modifyAlias(alias);
	}

	@Transactional
	public void deleteAlias(AnimeAlias alias) {
		animeDao.deleteAlias(alias);
	}
	
	/**
	 * alias不存在时，执行新增
	 * 反之更新
	 * @param alias
	 */
	@Transactional
	public void saveOrModifyAlias(AnimeAlias alias) {
		try{
			saveAlias(alias);	
		} catch (Exception e) {
			modifyAlias(alias);
		}
	}
	
	public Set<String> listSearchHistory(Long userId) {
		if(userId == null) {
			return null;
		}
		return redisTemplate.opsForSet().members("searchHistory" + ":" + userId);
	}

}
