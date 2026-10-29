package seedu.clublogistics.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.clublogistics.logic.Messages;
import seedu.clublogistics.model.equipment.Equipment;

/** Panel displaying the equipment register. */
public class EquipmentListPanel extends UiPart<Region> {
    private static final String FXML = "EquipmentListPanel.fxml";

    @FXML
    private ListView<Equipment> equipmentListView;

    @FXML
    private Label filterDescription;

    /** Creates a panel backed by the given observable equipment list. */
    public EquipmentListPanel(ObservableList<Equipment> equipmentList) {
        this(equipmentList, new ReadOnlyStringWrapper("Showing all equipment").getReadOnlyProperty());
    }

    /** Creates a panel with a persistent observable description of its view. */
    public EquipmentListPanel(ObservableList<Equipment> equipmentList, ReadOnlyStringProperty description) {
        this(equipmentList, description, equipmentList);
    }

    /** Creates a panel whose counts track both the filtered view and full register. */
    public EquipmentListPanel(ObservableList<Equipment> equipmentList, ReadOnlyStringProperty description,
                              ObservableList<Equipment> allEquipment) {
        super(FXML);
        filterDescription.textProperty().bind(Bindings.createStringBinding(() ->
                description.get() + "\n" + Messages.formatEquipmentCount(equipmentList.size(), allEquipment.size()),
                description, equipmentList, allEquipment));
        equipmentListView.setItems(equipmentList);
        equipmentListView.setCellFactory(listView -> new EquipmentListViewCell());
    }

    /** Displays an equipment card for each non-empty cell. */
    class EquipmentListViewCell extends ListCell<Equipment> {
        @Override
        protected void updateItem(Equipment equipment, boolean empty) {
            super.updateItem(equipment, empty);
            setText(null);
            setGraphic(empty || equipment == null ? null : new EquipmentCard(equipment).getRoot());
        }
    }
}
