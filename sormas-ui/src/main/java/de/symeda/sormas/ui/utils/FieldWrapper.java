package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

public interface FieldWrapper<T extends Component> {

	ComponentContainer wrap(T component);
}
