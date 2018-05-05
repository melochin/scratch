package scratch.dao;

import scratch.model.entity.Card;

import java.util.List;

public interface ICardRepository {

	List<Card> list(String brochureId);

	List<Card> listMemory(String brochureId);

	void memory(String brochureId, String cardId);

	void save(String brochureId, Card card);

	void modify(String brochureId, Card card);

	void delete(String brochureId, Card card);

	void swap(String brochureId, String firstCardId, String secondCardId);

	void swapBefore(String brochureId, String firstCardId, String secondCardId);

}
