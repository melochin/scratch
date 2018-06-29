package scratch.service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageRowBounds;

import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.support.FileUtils;
import scratch.support.PageFactory;
import scratch.support.service.PageBean;

@Service
public class AnimeService {

	@Autowired
	private ConversionService conversionService;

	/**
	 * @deprecated 将用图片服务代替
	 */
	private static final String UPLOAD_ANIME = "/upload/anime";
	
	private static final String SEARCH_HISTORY = "searchHistory";

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private IAnimeDao animeDao;

	@Autowired
	private IAnimeAliasDao aliasDao;

	@Autowired
	private IAnimeFocusDao focusDao;
	
	public List<Anime> list() {
		return animeDao.list();
	}

	/**
	 * 根据类型查询 anime,
	 * 排序：按照关注数量 Desc
	 * @param type
	 * @see scratch.model.DictType.ANIMETYPE
	 * @return
	 */
	public List<Anime> listByType(String type) {
		List<Anime> animes = animeDao.listIf(type, null);
		animes.sort(this::focusCompare);
		return animes;
	}
	
	public List<Anime> listByName(String name) {
		List<Anime> animes = animeDao.listByName(name);
		animes.sort(this::focusCompare);
		return animes;
	}

	/** 查询最受关注的Anime
	 *	@param limit 限制显示个数
	 */
	public List<Anime> listMostFocused(Integer limit) {
		List<Anime> animes = animeDao.listMostFocused();
		if(limit == null) return animes;
		return animes.stream().limit(limit).collect(Collectors.toList());
	}
	
	/**
	 * 将最受关注的Anime，根据类型分组
	 * @param limit 限制每组显示个数
	 */
	public Map<String,List<Anime>> listMostFcousedGroupByType(Integer limit) {

		List<Anime> animes = listMostFocused(null);
		Map<String, List<Anime>> animeMap = animes.stream()
				.collect(Collectors.groupingBy(Anime::getType));

		if(limit != null) {
			animeMap.entrySet().forEach(entry -> {
				List<Anime> limits = entry.getValue().stream()
						.limit(limit).collect(Collectors.toList());
				entry.setValue(limits);
			});
		}

		return animeMap;
	}
	
	public PageBean pageByType(String type, Integer page) {
		// type 空字串 当 null处理
		type = Optional.ofNullable(type)
				.filter(s -> !StringUtils.isEmpty(s))
				.orElse(null);
		PageRowBounds pageRowBounds = PageFactory.asList(page);
		return conversionService.convert(animeDao.pageByType(type, pageRowBounds), PageBean.class);
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
		file.createNewFile();
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
		if(!new File(realPath + UPLOAD_ANIME).isDirectory()) {
			new File(realPath + UPLOAD_ANIME).mkdirs();
		}
		return new File(FilenameUtils.concat(realPath + UPLOAD_ANIME,filename));
	}
	
	private void deleteFile(String filename, String realPath) {
		if(!StringUtils.isEmpty(filename)) {
			File file = new File(realPath + UPLOAD_ANIME, filename);
			file.delete();
		}
	}

	public Anime findByIdWithAlias(Long id) {
		Anime anime = animeDao.getById(id);
		anime.setAliass(aliasDao.list(id));
		return anime;
	}

	/*-------------------------别名操作--------------------------------*/

	/**
	 * 1. alias存在时，更新
	 * 2. alias不存在时，新增
	 * @param alias
	 */
	@Transactional
	public void saveOrModifyAlias(AnimeAlias alias) {
		if(aliasDao.find(alias.getAnimeId(), alias.getHostId()) != null) {
			saveAlias(alias);
		} else {
			modifyAlias(alias);
		}
	}

	@Transactional
	public void saveAlias(AnimeAlias alias) {
		aliasDao.save(alias);
	}

	@Transactional
	public void modifyAlias(AnimeAlias alias) {
		aliasDao.modify(alias);
	}

	@Transactional
	public void deleteAlias(AnimeAlias alias) {
		aliasDao.delete(alias);
	}

	/*-------------------------搜索记录--------------------------------*/

	/**
	 * 新增搜索记录（最多每人5条记录）
	 * @param name
	 * @param userId
	 */
	public void addSearchHistory(String name, Long userId) {
		String key = SEARCH_HISTORY + ":" + userId;
		SetOperations<String, Object> ops = redisTemplate.opsForSet();
		if(ops.size(key) >= 5) {
			ops.pop(key);
		}
		ops.add(key, name);
	}

	/**
	 * 显示搜索历史记录
	 * @param userId
	 * @return
	 */
	public Set<String> listSearchHistory(Long userId) {
		if(userId == null) return null;
		String key = "searchHistory" + ":" + userId;
		return redisTemplate.opsForSet().members(key)
				.stream().map(s -> s.toString()).collect(Collectors.toSet());
	}

	private int focusCompare(Anime first, Anime second) {
		return focusDao.count(first.getId()) >= focusDao.count(second.getId()) ? 1 : 0;
	}
}
