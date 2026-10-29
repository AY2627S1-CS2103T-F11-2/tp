package seedu.clublogistics.ui;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
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
        super(FXML);
        filterDescription.textProperty().bind(description);
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
