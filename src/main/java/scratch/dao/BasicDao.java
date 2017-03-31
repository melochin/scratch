package scratch.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import scratch.service.Page;

@Transactional
public class BasicDao<E> {

	@Autowired
	protected SessionFactory sessionFactory;
	
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	protected Criteria createCriteria(Class<?> clazz) {
		return getCurrentSession().createCriteria(clazz);
	}
	
	/**
	 * 新增对象
	 * @param o
	 */
	public void save(E o) {
		Session session = getCurrentSession();
		session.save(o);
	}
	
	/**
	 * 更新对象
	 * @param o
	 */
	public void update(E o){
		Session session = getCurrentSession();
		session.update(o);
	}
	
	/**
	 * 更新对象：根据主键
	 * @param o
	 * @param id
	 */
	public void update(E o, Serializable id) {
		if(id == null) {
			update(o);
			return;
		}
		try {
			E dbObject = getNewDBObject(o, id, o.getClass());
			update(dbObject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 新增/更新对象
	 * @param o
	 */
	public void saveOrUpdate(E o) {
		getCurrentSession().saveOrUpdate(o);
	}
	
	public void saveOrUpdate(List<E> list) {
		Session session = getCurrentSession();
		for(E o : list) {
			session.saveOrUpdate(o);
		}
	}
	
	/**
	 * 新增/更新对象
	 * @param o
	 * @param id
	 */
	public void saveOrUpdate(E o, Serializable id) {
		Session session = getCurrentSession();
		//Id如果不存在，则认为是新的对象
		if(id == null) {
			session.save(o);
			return;
		}
		//Id存在，读取数据库中的对象
		Object dbObject = null;
		try {
			dbObject = getNewDBObject(o, id, o.getClass());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		session.saveOrUpdate(dbObject);
	}
	
	private E getNewDBObject(E newObject, Serializable id, Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
		//获得数据库中的对象
		@SuppressWarnings("unchecked")
		E dbObject = (E) getCurrentSession().get(newObject.getClass(), id);
		if(dbObject == null) return newObject;
		updateObject(newObject, dbObject, clazz);
		return dbObject;
	}
	
	private void updateObject(Object newObject, Object oldObject, Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields) {
			f.setAccessible(true);
			Object newValue = f.get(newObject);
			if(newValue == null) {
				continue;
			}
			f.set(oldObject, newValue);
		}
	}
	
	/**
	 * 删除对象
	 * @param o
	 */
	public void remove(E o) {
		getCurrentSession().delete(o);
		return;
	}
	
	/**
	 * 删除对象：根据ID
	 * @param o
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public void remove(Class<?> clzz, Serializable id) {
		E o = (E) getCurrentSession().get(clzz, id);
		if(o == null) return;
		remove(o);
		return;
	}
	
	
	/**
	 * 获取单个对象
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E get(Class<E> clazz) {
		Session session = getCurrentSession();
		Criteria criteria = session.createCriteria(clazz);
		return (E) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public E get(Class<E> clzz, Serializable id) {
		Session session = getCurrentSession();
		return (E) session.get(clzz ,id);
	}
	

	/**
	 * 获取多个对象
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> list(Class<T> clazz) {
		Session session = getCurrentSession();
		Criteria criteria = session.createCriteria(clazz);
		return (List<T>) criteria.list();
	}
	
	/**
	 * 通过hql获取单个对象
	 * @param hql
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getByHql(String hql, Object...args) {
		return (T) createQuery(hql, args).uniqueResult();
	}
	
	/**
	 * 通过hql获取多个对象
	 * @param hql
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> listByHql(String hql, Object...args) {
		return (List<T>) createQuery(hql, args).list();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> listByCriteria(Criteria c, Page page) {
		if(c == null) {
			return null;
		}
		
		//如果要进行分页查询
		if(page != null) {
			//先获取总记录数
			c.setProjection(Projections.rowCount());
			page.setTotalItem(Long.valueOf(c.uniqueResult().toString()));
			c.setProjection(null);
			//过滤记录数
			c.setFirstResult(page.getFirstIndex())
				.setMaxResults(page.getPerPageItem());
		}
		return c.list();
	}
	
	
	/**
	 * 通过hql更新对象
	 * @param hql
	 * @param args
	 * @return 影响行数
	 */
	public int updateByHql(String hql, Object...args) {
		return createQuery(hql, args).executeUpdate();
	}
	
	/**
	 * 通过hql和参数，创建Query对象
	 * @param hql
	 * @param args
	 * @return
	 */
	private Query createQuery(String hql, Object...args) {
		int index = 0;
		Session session = getCurrentSession();
		Query query = session.createQuery(hql);
		for(Object o : args) {
			query.setParameter(index++, o);
		}
		return query;
	}
	
}
