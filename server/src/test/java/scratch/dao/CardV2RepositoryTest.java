package scratch.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.dao.redis.CardRepository;
import scratch.model.entity.Card;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class CardV2RepositoryTest extends ContextTest {

	@Autowired
	private CardRepository cardV2Repository;

	private final static String brochureId = "32a4523d-2256-461b-823c-15e6a34d2e19";

	@Test
	public void save() throws Exception {
		// 测试数据
		Card card = new Card("test", "test");
		int oldSize = cardV2Repository.list(brochureId).size();
		cardV2Repository.save(brochureId, card);
		int newSize = cardV2Repository.list(brochureId).size();

		// 判断brochure与card关联关系是否保存成功
		assertTrue(oldSize + 1 == newSize );
		// 判断cards是否保存成功
		assertTrue(cardV2Repository.find(card.getId()) != null);
	}

	@Test
	public void modify() throws Exception {
		Card card = new Card("test", "test");
		cardV2Repository.save(brochureId, card);
		card.setValue("new");
		cardV2Repository.modify(brochureId, card);
		assertTrue(cardV2Repository.find(card.getId()).getValue().equals("new"));
	}

	@Test
	public void delete() throws Exception {
		Card card = new Card("test", "test");
		cardV2Repository.save(brochureId, card);
		cardV2Repository.delete(brochureId, card);
		assertTrue(cardV2Repository.find(card.getId()) == null);
	}

	@Test
	public void swap() throws Exception {
		Card first = new Card("test", "test");
		Card second = new Card("test", "test");
		cardV2Repository.save(brochureId, first);
		cardV2Repository.save(brochureId, second);
		cardV2Repository.swap(brochureId, first.getId(), second.getId());
	}


	@Test
	public void list() throws Exception {
		System.out.println(cardV2Repository.list("32a4523d-2256-461b-823c-15e6a34d2e19"));
	}

}