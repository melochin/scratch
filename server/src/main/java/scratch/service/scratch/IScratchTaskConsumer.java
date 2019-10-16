package scratch.service.scratch;

import org.springframework.amqp.core.Message;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeEpisode;

import java.io.IOException;
import java.util.List;

public interface IScratchTaskConsumer {

	int consumeInstant(Message message, List<Anime> animes);

	void consumeTiming(Message message, Anime anime);

	int save(List<AnimeEpisode> animeEpisodeScratchList);

}
