package scratch.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageRowBounds;

import scratch.dao.inter.IAnimeDao;
import scratch.model.Anime;
import scratch.support.FileUtils;
import scratch.support.PageFactory;

@Service
public class AnimeService {

	private static final String UPLOAD_ANIME = "/upload/anime";
	@Autowired
	private IAnimeDao animeDao;
	
	public List<Anime> find(int page) {
		PageRowBounds pageRowBounds = PageFactory.asList(page);  
		return animeDao.find(pageRowBounds);
	}
	
	public List<Anime> findAll() {
		return animeDao.findAll();
	}
	
	public void save(Anime anime) {
		animeDao.save(anime);
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
	
	public void update(Anime anime) {
		animeDao.update(anime);
	}
	
	public void updateWithFile(Anime anime, MultipartFile multipartFile, String realPath) 
			throws IllegalStateException, IOException  {
		//删除老的文件
		Anime originAnime = animeDao.findById(anime.getId());
		deleteFile(originAnime.getPic(), realPath);
		
		//更新文件名
		File file = getFile(multipartFile, realPath);
		anime.setPic(file.getName());
		update(anime);
		
		//保存文件
		multipartFile.transferTo(file);
	}
	
	public void delete(Long animeId) {
		animeDao.delete(animeId);
	}

	public Anime findById(Long animeId) {
		return animeDao.findById(animeId);
	}

	public void deleteWithFile(Long animeId, String realPath) {
		Anime anime = animeDao.findById(animeId);
		deleteFile(anime.getPic(), realPath);
		delete(animeId);
	}
	
	public File getFile(MultipartFile multipartFile, String realPath) {
		//获取表单上传文件的后缀名
		String originFilename = multipartFile.getOriginalFilename();
		String suffix = FileUtils.getSuffix(originFilename);
		//使用UUID作为文件名
		String filename = UUID.randomUUID().toString() + "." + suffix;
		return new File(FilenameUtils.concat(realPath + UPLOAD_ANIME, filename));
	}
	
	
	private void deleteFile(String filename, String realPath) {
		if(!StringUtils.isEmpty(filename)) {
			//获取完整路径
			String path = FilenameUtils.concat(realPath + UPLOAD_ANIME, filename);
			//删除文件
			org.apache.commons.io.FileUtils.deleteQuietly(new File(path));
		}
	}

}
