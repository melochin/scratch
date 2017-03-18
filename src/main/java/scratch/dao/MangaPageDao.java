package scratch.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import scratch.model.MangaPage;

@Repository
public class MangaPageDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	public boolean save(MangaPage mp){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try{
			session.saveOrUpdate(mp);
		}catch (Exception e) {
			session.getTransaction().rollback();
			session.close();
			return false;
		}
		session.getTransaction().commit();
		session.close();
		return true;
	}
	
	public List<MangaPage> getPages(long mangaId){
		Session session = sessionFactory.openSession();
		return session.createQuery("from MangaPage where mangaId = :mangaId")
				.setParameter("mangaId", mangaId)
				.list();
	}
	
	public static void main(String[] args) {
		ApplicationContext ap = new ClassPathXmlApplicationContext("applicationcontext.xml");
		MangaPageDao dao = (MangaPageDao)ap.getBean("mangaPageDao");
	}
	
}
