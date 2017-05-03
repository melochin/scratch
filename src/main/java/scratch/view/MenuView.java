package scratch.view;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;

public class MenuView implements ViewPreparer {

	@Override
	public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
		attributeContext.putAttribute("nav", new Attribute("/WEB-INF/jsp/common/nav-user.jsp"));
	}

}
