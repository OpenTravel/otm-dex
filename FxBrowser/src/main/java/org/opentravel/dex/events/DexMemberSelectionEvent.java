/**
 * 
 */
package org.opentravel.dex.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.dex.controllers.member.MemberDAO;
import org.opentravel.model.OtmObject;
import org.opentravel.model.otmFacets.OtmContributedFacet;
import org.opentravel.model.otmLibraryMembers.OtmLibraryMember;

import javafx.event.EventType;
import javafx.scene.control.TreeItem;

/**
 * OTM DEX event for signaling when a library member has been selected.
 * 
 * @author dmh
 *
 */
public class DexMemberSelectionEvent extends DexEvent {
	private static Log log = LogFactory.getLog(DexMemberSelectionEvent.class);
	private static final long serialVersionUID = 20190409L;

	public static final EventType<DexMemberSelectionEvent> MEMBER_SELECTED = new EventType<>(DEX_ALL,
			"MEMBER_SELECTED");

	private final OtmLibraryMember member;

	public OtmLibraryMember getMember() {
		return member;
	}

	/**
	 * Filter change event with no subject.
	 */
	public DexMemberSelectionEvent() {
		super(MEMBER_SELECTED);
		member = null;
	}

	/**
	 * A library member selection event.
	 * 
	 * @param source
	 *            is the controller that created the event
	 * @param target
	 *            the tree item that was selected
	 */
	public DexMemberSelectionEvent(Object source, TreeItem<MemberDAO> target) {
		super(source, target, MEMBER_SELECTED);
		log.debug("DexEvent source/target constructor ran.");
		// If there is data, extract it from target
		OtmObject m = null;
		if (target != null && target.getValue() != null && target.getValue().getValue() != null)
			m = target.getValue().getValue();
		if (m instanceof OtmContributedFacet)
			m = ((OtmContributedFacet) m).getContributor();
		if (m != null && !(m instanceof OtmLibraryMember))
			m = m.getOwningMember();
		member = (OtmLibraryMember) m;
	}

	/**
	 * @param otm
	 */
	public DexMemberSelectionEvent(OtmLibraryMember otm) {
		super(MEMBER_SELECTED);
		log.debug("DexEvent OtmModelElement constructor ran.");
		if (otm instanceof OtmContributedFacet)
			otm = ((OtmContributedFacet) otm).getContributor();
		member = otm;
	}

}
