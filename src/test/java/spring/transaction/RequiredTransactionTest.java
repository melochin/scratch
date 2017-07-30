package spring.transaction;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import scratch.dao.inter.IAnimeDao;
import scratch.model.Anime;
import scratch.test.ContextTest;

public class RequiredTransactionTest extends ContextTest {

	@Autowired
	private PlatformTransactionManager txManager;
	
	@Autowired
	private IAnimeDao animeDao;

	/**
	 * 内部事务回滚，外部事务提交，抛出异常
	 */
//	@Test(expected=UnexpectedRollbackException.class)
	public void innerRollbackOuterCommitTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRED");
		try{
			createNewTransactionAndRollback("PROPAGATION_REQUIRED");
		} catch (Exception e) {
		}
		txManager.commit(status);
	}
	
	/**
	 * 内部事务回滚，外部事务回滚，所有事务回滚
	 */
//	@Test
	public void innerRollbackOuterRollbackTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRED");
		try{
			createNewTransactionAndRollback("PROPAGATION_REQUIRED");
		} catch (Exception e) {
		}
		txManager.rollback(status);
	}
	
	/**
	 * 内部事务提交，外部事务回滚，所有事务回滚
	 */
//	@Test
	public void innerCommitOuterRollbackTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRED");
		int beforeCount = animeDao.findAll().size(); 
		try{
			createNewTransactionAndCommit("PROPAGATION_REQUIRED");
		} catch (Exception e) {
		}
		txManager.rollback(status);
		int afterCount = animeDao.findAll().size();
		Assert.assertTrue(beforeCount == afterCount);
	}
	
	/**
	 * 内部事务提交，外部事务提交，所有事务提交
	 */
	@Test
	public void innerCommitOuterCommitTest() {
		TransactionStatus status = createTransacation("PROPAGATION_REQUIRED");
		int beforeCount = animeDao.findAll().size(); 
		try{
			createNewTransactionAndCommit("PROPAGATION_REQUIRED");
			Assert.assertTrue(beforeCount + 1 == animeDao.findAll().size());
			animeDao.save(new Anime(UUID.randomUUID().toString()));
		} catch (Exception e) {
		}
		txManager.commit(status);
		int afterCount = animeDao.findAll().size();
		Assert.assertTrue(beforeCount + 2 == afterCount);
	}
	
	private void createNewTransactionAndRollback(String propagationBehaviorName) {
		TransactionStatus status = createTransacation(propagationBehaviorName);
		txManager.rollback(status);
	}
	
	private void createNewTransactionAndCommit(String propagationBehaviorName) {
		TransactionStatus status = createTransacation(propagationBehaviorName);
		animeDao.save(new Anime("test"));
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
		return txManager.getTransaction(def);
	}
	
	
}
