package scratch.service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageRowBounds;

import scratch.dao.inter.IAnimeAliasDao;
import scratch.dao.inter.IAnimeDao;
import scratch.dao.inter.IAnimeEpisodeDao;
import scratch.dao.inter.IAnimeFocusDao;
import scratch.model.RedisKey;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;
import scratch.model.ohter.UserAdapter;
import scratch.model.view.AnimeDisplay;
import scratch.support.FileUtils;
import scratch.support.PageFactory;
import scratch.support.service.PageBean;

import static java.util.stream.Collectors.toList;

@Service
public class AnimeService {

	@Autowired
	private ConversionService conversionService;

	/**
	 * @deprecated 将用图片服务代替
	 */
	private static final String UPLOAD_ANIME = "/WEB-INF/resource/upload/anime";
	
	private static final String SEARCH_HISTORY = "searchHistory";

	@Autowired
	private WebFileService fileService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private IAnimeDao animeDao;

	@Autowired
	private IAnimeAliasDao aliasDao;

	@Autowired
	private IAnimeFocusDao focusDao;

	@Autowired
	private IAnimeEpisodeDao episodeDao;

	@Value("${pic.url}")
	private String url;

	/*-----------------------查询---------------------------*/

	/**
	 * 按animeId查询
	 * @param animeId
	 * @return
	 */
	public Anime findById(Long animeId) {
		return animeDao.getById(animeId);
	}

	/**
	 * 按animeId查询，　返回的Anime含有Aliass
	 * @param animeId
	 * @return
	 */
	public Anime findByIdWithAlias(Long animeId) {
		Anime anime = animeDao.getById(animeId);
		anime.setAliass(aliasDao.list(animeId));
		return anime;
	}

	public List<AnimeAlias> findAlias(Long animeId) {
		return aliasDao.list(animeId);
	}

	/**
	 * 查询所有的Anime
	 * @return
	 */
	public List<Anime> list() {
		return animeDao.list();
	}

	/**
	 * 根据类型查询 anime,
	 * 排序：按照关注数量 desc
	 * @param type
	 * @see scratch.model.DictType.ANIMETYPE
	 * @return
	 */
	public List<Anime> listByType(String type) {
		List<Anime> animes = animeDao.listIf(type, null);
		animes.sort(this::focusCompare);
		return animes;
	}

	/**
	 *
	 * 按名称或者类型查询（名称和类型可空）
	 * @param name
	 * @param type
	 * @return
	 */
	public List<AnimeDisplay> list(@Nullable String name, @Nullable String type, UserAdapter userAdapter) {
		List<Anime> animes = null;

		if (name == null && type == null) {
			animes = list();
		}

		else if (name == null && type != null) {
			animes = listByType(type);
		}

		else if (name != null) {
			animes = animeDao.listByName(name.trim());
			if (type != null) {
				animes = animes.stream()
						.filter(anime -> type.equals(anime.getType()))
						.collect(toList());
			}
		}

		animes.sort(this::focusCompare);
		List<AnimeDisplay> animeDisplays = animes.stream()
				.map(anime -> convertAnimeDisplay(anime, userAdapter))
				.collect(toList());

		return animeDisplays;
	}

	public List<Anime> listMostFocused() {
		return listMostFocused(null);
	}

	/** 查询最受关注的Anime
	 *	@param limit 限制显示个数
	 */
	public List<Anime> listMostFocused(Integer limit) {
		return focusDao.listMostFocused(limit)
				.stream()
				.map(id -> animeDao.getById(id))
				.collect(toList());
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
						.limit(limit).collect(toList());
				entry.setValue(limits);
			});
		}

		return animeMap;
	}

	/**
	 * 分页
	 * @param type
	 * @param page
	 * @return
	 */
	public PageBean pageByType(String type, Integer page) {
		// type 空字串 当 null处理
		type = Optional.ofNullable(type)
				.filter(s -> !StringUtils.isEmpty(s))
				.orElse(null);
		PageRowBounds pageRowBounds = PageFactory.asList(page);
		return conversionService.convert(animeDao.pageByType(type, pageRowBounds), PageBean.class);
	}
	
	@Transactional
	public void save(Anime anime) {
		// 保存Anime
		animeDao.save(anime);

		// 保存Alias
		saveAnimeAliases(anime.getId(), anime.getAliases());
	}

	/**
	 * 目前都是涉及后台的修改操作，更新考虑到alias
	 * 不管alias是否为null，都先清除别名
	 * 然后保存别名
	 * @param anime
	 */
	@Transactional
	public void update(Anime anime) {
		// 更新Anime
		animeDao.update(anime);

		// 更新Alias
		aliasDao.delete(anime.getId());
		saveAnimeAliases(anime.getId(), anime.getAliases());
	}
	
	@Transactional
	public void delete(Long id) {
		Anime anime = animeDao.getById(id);
		// 删除数据
		animeDao.delete(id);
		aliasDao.delete(id);
		// 删除文件
		String filename = anime.getPic();
		new File(url + filename).delete();
	}

	@Transactional
	public void saveAnimeAliases(Long animeId, List<AnimeAlias> animeAliases) {
		if(animeAliases == null) return;

		animeAliases = animeAliases.stream()
				.filter(animeAlias -> StringUtils.isEmpty(animeAlias.getAnimeId()) == false)
				.filter(animeAlias -> animeAlias.getHostId() != null)
				.collect(toList());
		if(animeAliases.size() == 0) return;

		for(AnimeAlias animeAlias : animeAliases) {
			aliasDao.save(animeAlias);
		}
	}


	/*-------------------------图片处理--------------------------------*/

	/**
	 * 
	 * @param anime
	 * @param multipartFile
	 * @return
	 * @throws Exception 
	 */
	public void saveWithPicFile(Anime anime, MultipartFile multipartFile)
			throws IOException  {

		try {
			File file = fileService.save(UPLOAD_ANIME, multipartFile);
			anime.setPic(file.getName());
		} catch (IOException e) {
			anime.setPic(null);
			e.printStackTrace();
			throw new IOException("记录成功保存，但是图片上传失败");
		} finally {
			save(anime);
		}
	}
	
	public void updateWithFile(Anime anime, MultipartFile multipartFile)
			throws IOException  {
		//删除老的文件
		String oldFilename = animeDao.getById(anime.getId()).getPic();
		fileService.delete(UPLOAD_ANIME, oldFilename);

		//更新文件名
		try{
			File file = fileService.save(UPLOAD_ANIME, multipartFile);
			anime.setPic(file.getName());
		} catch (IOException e) {
			anime.setPic(null);
			e.printStackTrace();
			throw new IOException("记录更新成功，但是图片保存失败");
		} finally {
			update(anime);
		}

	}

	public String upload(MultipartFile multipartFile) throws IOException {
		String suffix = FileUtils.getSuffix(multipartFile.getOriginalFilename());
		File file = FileUtils.save(url + FileUtils.generateUUIDFilename(suffix), multipartFile);
		return file.getName();
	}

	public void deleteWithFile(Long animeId) {
		Anime anime = animeDao.getById(animeId);
		animeDao.delete(animeId);

		String filename = anime.getPic();
		fileService.delete(UPLOAD_ANIME, filename);
	}

	/*-------------------------类型转换---------------------------------*/
	public AnimeDisplay convertAnimeDisplay(Anime anime, UserAdapter userAdapter) {
		// 类型转换
		AnimeDisplay animeDisplay = new AnimeDisplay(anime);
		// 设置 上次更新时间
		animeDisplay.setUpdateTime(episodeDao.getLastUpdatedTime(anime.getId()));

		// 用户没有登录的时候，原样返回
		if (userAdapter == null) return animeDisplay;

		// 用户登录时，查找关注状态
		boolean isFocus = focusDao.findByAnimeAndUser(anime.getId(), userAdapter.getUserId()) != null;
		animeDisplay.setFocus(isFocus);

		return animeDisplay;
	}

	/*-------------------------别名操作--------------------------------*/

	/*-------------------------搜索记录--------------------------------*/

	/**
	 * 新增搜索记录（最多每人5条记录）
	 * @param name
	 * @param userId
	 */
	public void addSearchHistory(String name, Long userId) {
		String key = RedisKey.searchHistory(userId.toString());
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

		String key = RedisKey.searchHistory(userId.toString());
		return redisTemplate.opsForSet().members(key)
				.stream().map(s -> s.toString()).collect(Collectors.toSet());
	}

	/**
	 * 按关注人数排序
	 * @param first
	 * @param second
	 * @return
	 */
	private int focusCompare(Anime first, Anime second) {
		return focusDao.count(first.getId()) - focusDao.count(second.getId());
	}
}
