package de.symeda.sormas.api.event;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.utils.IgnoreForUrl;

/**
 * Criteria filter for actions related to en event.
 */
public class EventActionCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 5981720569585071844L;

	private EventReferenceDto event;
	private ActionStatus actionStatus;

	@IgnoreForUrl
	public EventReferenceDto getEvent() {
		return event;
	}

	public EventActionCriteria event(EventReferenceDto event) {
		this.event = event;
		return this;
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public EventActionCriteria actionStatus(ActionStatus actionStatus) {
		setActionStatus(actionStatus);
		return this;
	}

	public ActionCriteria toActionCriteria() {
		return new ActionCriteria().event(event).actionStatus(actionStatus);
	}
}
