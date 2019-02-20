/**
 * 
 */
package OTM_FX.FxBrowser;

import java.util.ArrayList;
import java.util.List;

import org.opentravel.model.otmProperties.UserSelectablePropertyTypes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author dmh
 */
@SuppressWarnings("restriction")
public class DemoNode {
	/**
	 * 
	 */
	private String name;
	StringProperty trueName = new SimpleStringProperty("");

	/**
	 * 
	 */
	private StringProperty assignedType = new SimpleStringProperty("");

	/**
	 * 
	 */
	private UserSelectablePropertyTypes nodeType;
	private final SimpleStringProperty description = new SimpleStringProperty("");
	// private String description;

	/**
	 * 
	 */
	public DemoNode() {
		// Make the name change when the type does
		trueName.bind(assignedType);
		// TODO - learn how to assign my methods
	}

	public DemoNode(String name, String assignedType, String nodeType) {
		this(name, assignedType, nodeType, "");
	}

	public DemoNode(String name, String assignedType, String nodeType, String description) {
		this.nodeType = UserSelectablePropertyTypes.lookup(nodeType);
		setAssignedType(assignedType);
		setName(name);
		setTrueName(name);
		trueName.bind(this.assignedType);
		setDescription(description);
	}

	public String getAssignedType() {
		return assignedType.getValue();
	}

	public String getDescription() {
		return description.getValue();
	}

	public String getName() {
		// return name;
		return getTrueName();
	}

	public List<DemoNode> getNodes() {
		List<DemoNode> list = new ArrayList<>();
		list.add(new DemoNode("A", "String", "Element", "Description 1"));
		list.add(new DemoNode("b", "String", "attribute", "Description 2"));
		list.add(new DemoNode("C", "String", "Element", "Description 3"));
		list.add(new DemoNode("D", "String", "ElementRef", "Description 4"));
		list.add(new DemoNode("E", "String", "Indicator", "Description 5"));
		return list;
	}

	public String getNodeType() {
		return nodeType.label();
	}

	public String getTrueName() {
		return trueName.getValue();
	}

	public void setAssignedType(String assignedType) {
		this.assignedType.setValue(assignedType);
	}

	public void setDescription(String description) {
		this.description.setValue(description);
	}

	public void setName(String name) {
		// this.name = name;
		setTrueName(name);
	}

	public void setNodeType(String nodeType) {
		this.nodeType = UserSelectablePropertyTypes.lookup(nodeType);
	}

	public void setTrueName(String trueName) {
		this.trueName.setValue(trueName);
	}

}