package dk.osaa.psaw.web.view;

import lombok.Getter;
import dk.osaa.psaw.web.api.StartPageData;
import io.dropwizard.views.View;

public class StartPageDataView extends View {
	@Getter
	private final StartPageData startPageData;
	
	public StartPageDataView(StartPageData startPageData) {
		super("startpage.ftl");
		this.startPageData = startPageData;
	}
}
