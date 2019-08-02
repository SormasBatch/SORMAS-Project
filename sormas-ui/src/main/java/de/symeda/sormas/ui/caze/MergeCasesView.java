package de.symeda.sormas.ui.caze;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class MergeCasesView extends AbstractView {

	public static final String VIEW_NAME = CasesView.VIEW_NAME + "/merge";

	private CaseCriteria criteria;

	private MergeCasesGrid grid;

	public MergeCasesView() {
		super(VIEW_NAME);

		boolean criteriaUninitialized = !ViewModelProviders.of(MergeCasesView.class).has(CaseCriteria.class);

		criteria = ViewModelProviders.of(MergeCasesView.class).get(CaseCriteria.class);
		if (criteriaUninitialized) {
			criteria.creationDateFrom(DateHelper.subtractDays(new Date(), 30))
			.creationDateTo(new Date())
			.region(UserProvider.getCurrent().getUser().getRegion());
		}

		grid = new MergeCasesGrid();
		grid.setCriteria(criteria);

		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(new MergeCasesFilterComponent(criteria, () -> {
			if (ViewModelProviders.of(MergeCasesView.class).has(CaseCriteria.class)) {
				grid.reload();
			} else {
				navigateTo(null);
			}
		}));
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		addComponent(gridLayout);

		Button btnOpenGuide = new Button(I18nProperties.getCaption(Captions.caseOpenMergeGuide));
		btnOpenGuide.setId("openMergeGuide");
		btnOpenGuide.setIcon(VaadinIcons.QUESTION);
		btnOpenGuide.addClickListener(e -> buildAndOpenMergeInstructions());
		addHeaderComponent(btnOpenGuide);
		
		Button btnBack = new Button(I18nProperties.getCaption(Captions.caseBackToDirectory));
		btnBack.setId("backToDirectory");
		btnBack.setIcon(VaadinIcons.ARROW_BACKWARD);
		btnBack.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnBack.addClickListener(e -> ControllerProvider.getCaseController().navigateToIndex());
		addHeaderComponent(btnBack);
	}
	
	private void buildAndOpenMergeInstructions() {
		Window window = VaadinUiUtil.showPopupWindow(new MergeInstructionsLayout());
		window.setWidth(1024, Unit.PIXELS);
		window.setCaption(I18nProperties.getString(Strings.headingMergeGuide));
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
	}

}