package de.symeda.sormas.api.action;

import java.io.Serializable;

/**
 * Stats of actions for an entity : how many actions by status.
 */
public class ActionStatEntry implements Serializable {

	private static final long serialVersionUID = -4077086895569016018L;

	private ActionStatus actionStatus;
	private Long count;

	public ActionStatEntry(Long count, ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
		this.count = count;
	}

	public ActionStatEntry() {
	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
