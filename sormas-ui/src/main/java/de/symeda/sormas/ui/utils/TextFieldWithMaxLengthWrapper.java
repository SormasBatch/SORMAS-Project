package de.symeda.sormas.ui.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class TextFieldWithMaxLengthWrapper<T extends AbstractTextField> implements FieldWrapper<T> {

	@Override
	public ComponentContainer wrap(T textField) {

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		layout.addStyleName(CssStyles.FIELD_WRAPPER);

		textField.setWidth(100, Sizeable.Unit.PERCENTAGE);
		textField.addStyleName(CssStyles.RESIZABLE);
			textField.getValidators()
				.stream()
				.filter(v -> v instanceof MaxLengthValidator)
				.findFirst()
				.map(v -> ((MaxLengthValidator) v).getMaxLength())
			.ifPresent(textField::setMaxLength);
		textField.setNullRepresentation("");
		textField.setTextChangeTimeout(200);

		Label labelField = new Label();
		labelField.setId(textField.getId() + "_label");
		labelField.setWidth(100, Sizeable.Unit.PERCENTAGE);
		labelField.addStyleNames(CssStyles.ALIGN_RIGHT, CssStyles.FIELD_EXTRA_INFO, CssStyles.LABEL_ITALIC);

		textField.addTextChangeListener(e -> {
			labelField.setValue(buildLabelMessage(e.getText(), textField));
			ajustRows(textField, e.getText());
		});
		textField.addValueChangeListener(e -> {
			labelField.setValue(buildLabelMessage(textField.getValue(), textField));
			ajustRows(textField, textField.getValue());
		});

		layout.addComponents(textField, labelField);

		return layout;
	}

	private String buildLabelMessage(String text, T textField) {
		return String.format(I18nProperties.getCaption(Captions.numberOfCharacters), Strings.nullToEmpty(text).length(), textField.getMaxLength());
	}

	private void ajustRows(T textField, String text) {
		if (textField instanceof TextArea) {
			((TextArea) textField).setRows(Math.min(30, Math.max(CharMatcher.is('\n').countIn(Strings.nullToEmpty(text)), 3) + 1));
		}
	}
}
