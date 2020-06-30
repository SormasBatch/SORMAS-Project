/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.action;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.RichTextArea;

import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public class ActionEditForm extends AbstractEditForm<ActionDto> {

	private static final long serialVersionUID = 1L;

	//@formatter:off
	private static final String HTML_LAYOUT = 
			fluidRow(
					loc(ActionDto.ACTION_CONTEXT), 
					locs(ActionDto.EVENT)) +
			fluidRowLocs(ActionDto.DATE, ActionDto.PRIORITY) +
			fluidRowLocs(ActionDto.DESCRIPTION) +
			fluidRowLocs(ActionDto.REPLY) +
			fluidRowLocs(ActionDto.ACTION_STATUS);
	//@formatter:off

	public ActionEditForm(boolean create) {

		super(ActionDto.class, ActionDto.I18N_PREFIX);
		addValueChangeListener(e -> {
			updateByActionContext();
			updateByCreating();
		});

		setWidth(680, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {

		addField(ActionDto.EVENT, ComboBox.class);
		DateTimeField date = addDateField(ActionDto.DATE, DateTimeField.class, -1);
		date.setImmediate(true);
		addField(ActionDto.PRIORITY, ComboBox.class);
		addField(ActionDto.ACTION_STATUS, OptionGroup.class);
		OptionGroup actionContext = addField(ActionDto.ACTION_CONTEXT, OptionGroup.class);
		actionContext.setImmediate(true);
		actionContext.addValueChangeListener(event -> updateByActionContext());
		// XXX: set visible when other contexts will be managed
		actionContext.setVisible(false);

		RichTextArea description = addField(ActionDto.DESCRIPTION, RichTextArea.class);
		description.setNullRepresentation("");
		description.setImmediate(true);
		RichTextArea reply = addField(ActionDto.REPLY, RichTextArea.class);
		reply.setNullRepresentation("");
		reply.setImmediate(true);

		setRequired(true, ActionDto.ACTION_CONTEXT, ActionDto.DATE);
		setReadOnly(true, ActionDto.ACTION_CONTEXT, ActionDto.EVENT);
	}

	private void updateByCreating() {

		ActionDto value = getValue();
		if (value != null) {
			boolean creating = value.getCreationDate() == null;

			UserDto user = UserProvider.getCurrent().getUser();
			boolean creator = user.equals(value.getCreatorUser());

			setVisible(!creating, ActionDto.REPLY);
			if (creating) {
				discard(ActionDto.REPLY);
			}

			setReadOnly(!creator, ActionDto.DESCRIPTION);
		}
	}

	private void updateByActionContext() {
		ActionContext actionContext = (ActionContext) getFieldGroup().getField(ActionDto.ACTION_CONTEXT).getValue();

		// context reference depending on action context
		// ready for adding new context
		ComboBox eventField = (ComboBox) getFieldGroup().getField(ActionDto.EVENT);
		if (actionContext != null) {
			switch (actionContext) {
			case EVENT:
				FieldHelper.setFirstVisibleClearOthers(eventField);
				FieldHelper.setFirstRequired(eventField);
				break;
			}
		} else {
			FieldHelper.setFirstVisibleClearOthers(null, eventField);
			FieldHelper.setFirstRequired(null,  eventField);
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
