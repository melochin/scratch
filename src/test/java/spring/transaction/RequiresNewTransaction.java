package spring.transaction;

import java.util.UUID;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import scratch.dao.inter.IAnimeDao;
import scratch.model.Anime;
import scratch.test.ContextTest;

@FixMethodOrder(MethodSorters.DEFAULT)
public class RequiresNewTransaction extends ContextTest {

	@Autowired
	private PlatformTransactionManager txManager;
	
	@Autowired
	private IAnimeDao animeDao;

	/**
	 * 内部事务回滚，外部事务提交
	 */
	//@Test
	public void innerRollbackOuterCommitTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRES_NEW");
		int count = animeDao.findAll().size();
		try{
			createNewTransactionAndRollback("PROPAGATION_REQUIRES_NEW");
			Assert.assertTrue(count == animeDao.findAll().size());
			animeDao.save(new Anime(UUID.randomUUID().toString()));
		} catch (Exception e) {
		}
		txManager.commit(status);
		Assert.assertTrue(count + 1 == animeDao.findAll().size());
	}
	
	/**
	 * 内部事务回滚，外部事务回滚
	 */
	//@Test
	public void innerRollbackOuterRollbackTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRES_NEW");
		int count = animeDao.findAll().size();
		try{
			createNewTransactionAndRollback("PROPAGATION_REQUIRES_NEW");
			Assert.assertTrue(count == animeDao.findAll().size());
			animeDao.save(new Anime(UUID.randomUUID().toString()));
		} catch (Exception e) {
		}
		txManager.rollback(status);
		Assert.assertTrue(count == animeDao.findAll().size());
	}
	
	/**
	 * 内部事务提交，外部事务回滚
	 * 两个独立的事务，外部事务比较早开启，内部事务发生变化，外部事务无法得只变化情况。
	 */
	@Test
	public void innerCommitOuterRollbackTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRES_NEW");
		int count = animeDao.findAll().size(); 
		try{
			createNewTransactionAndCommit("PROPAGATION_REQUIRES_NEW");	
			Assert.assertTrue(count + 1 == animeDao.findAll().size());
		} catch (Exception e) {
			txManager.rollback(status);
		}
		txManager.rollback(status);
		Assert.assertTrue(count + 1 == animeDao.findAll().size());
	}
	
	/**
	 * 内部事务提交，外部事务提交
	 */
	//@Test
	public void innerCommitOuterCommitTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRES_NEW");
		int count = animeDao.findAll().size(); 
		try{
			createNewTransactionAndCommit("PROPAGATION_REQUIRES_NEW");
			Assert.assertTrue(count + 1 == animeDao.findAll().size());
		} catch (Exception e) {
		}
		txManager.commit(status);
		Assert.assertTrue(count + 2 == animeDao.findAll().size());
	}
	
	private void createNewTransactionAndRollback(String propagationBehaviorName) {
		TransactionStatus status = createTransacation(propagationBehaviorName);
		animeDao.save(new Anime(UUID.randomUUID().toString()));
		txManager.rollback(status);
	}
	
	private void createNewTransactionAndCommit(String propagationBehaviorName) {
		TransactionStatus status = createTransacation(propagationBehaviorName);
	//	Anime anime = new Anime(new Long(129));
	//	anime.setName("T12345");
		animeDao.save(new Anime(UUID.randomUUID().toString()));
		txManager.commit(status);
	}
	
	/**
	 * PROPAGATION_REQUIRED
	 * PROPAGATION_REQUIRES_NEW
	 * @param propagationBehaviorName
	 * @return
	 */
	private TransactionStatus createTransacation(String propagationBehaviorName) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehaviorName(propagationBehaviorName);
	//	def.setIsolationLevelName("ISOLATION_READ_COMMITTED");
		return txManager.getTransaction(def);
	}
	
	
}
