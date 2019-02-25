/**
 * 
 */
package OTM_FX.FxBrowser;

// @SuppressWarnings("restriction")
// public class RoleCellFactory<P, S>
// implements Callback<TreeTableColumn<MemberPropertiesTableDAO, String>, TreeTableCell<MemberPropertiesTableDAO,
// String>> {
//
// private EventHandler<ActionEvent> deletePersonsHandler;
//
// @Override
// public TreeTableCell<MemberPropertiesTableDAO, String> call(TreeTableColumn<MemberPropertiesTableDAO, String> param)
// {
// return new ComboBoxTreeTableCell<MemberPropertiesTableDAO, String>(UserSelectablePropertyTypes.getObservableList()) {
// // Define inner class to handle cell
// {
// // Note - this context menu is specific to the role column.
// ContextMenu cm = new ContextMenu();
// MenuItem deletePersonsMenuItem = new MenuItem("Delete");
// // deletePersonsMenuItem.setOnAction( PersonTypeCellFactory.this.deletePersonsHandler );
// cm.getItems().add(deletePersonsMenuItem);
// this.setContextMenu(cm);
// //
// // this.getItems().addAll( "Friend", "Co-worker", "Other" );
// //
// this.setEditable(true);
// }
//
// @Override
// public void updateItem(String arg0, boolean empty) {
// super.updateItem(arg0, empty);
// if (!empty) {
// this.setText(arg0);
// } else {
// this.setText(null); // clear from recycled obj
// }
// }
//
// @SuppressWarnings("unchecked")
// @Override
// public void commitEdit(String newValue) {
// super.commitEdit(newValue);
// TreeTableRow<MemberPropertiesTableDAO> row = this.getTreeTableRow();
// MemberPropertiesTableDAO p = row.getItem();
//
// System.out.println("TODO - make " + p + " into a " + newValue);
//
// // TODO - study TASK!!!
// // Task<Void> task = new Task<Void>() {
// // @Override
// // protected Void call() {
// //// dao.updatePerson(p); // updates AR too
// // return null;
// // }
// // };
// // new Thread(task).start();
// }
// };
// }
//
// public void setDeletePersonsHandler(EventHandler<ActionEvent> handler) {
// this.deletePersonsHandler = handler;
// }
// }