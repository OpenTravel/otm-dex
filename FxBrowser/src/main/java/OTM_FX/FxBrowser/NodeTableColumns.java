/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package OTM_FX.FxBrowser;

/**
 * Table column ids, user displayed label, and associated property name
 * 
 * @author Dave Hollander
 * 
 */
public enum NodeTableColumns {

	FACET("Facet", "FacetColumn", "nodeType"), NAME("NameE", "NameColumn", "name"), ROLE(
			"Role",
			"RoleColumn",
			"nodeType"), TYPE("Type", "TypeColumn", "assignedType"), DESC(
			"Description",
			"DescriptionColumn",
			"description");

	private String label; // column label
	private String id; // id from FXML
	private String propertyName; // bean property name

	NodeTableColumns(String label, String id, String propertyName) {
		this.label = label;
		this.propertyName = propertyName;
		this.id = id;
	}

	public String label() {
		return label;
	}

	public String id() {
		return id;
	}

	public String property() {
		return propertyName;
	}

	public static NodeTableColumns lookup(String id) {
		for (NodeTableColumns v : values())
			if (v.id.equals(id))
				return v;
		return null;
	}
}
