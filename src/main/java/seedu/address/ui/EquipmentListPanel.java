package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.equipment.Equipment;

/** Panel displaying equipment IDs and names. */
public class EquipmentListPanel extends UiPart<Region> {
    private static final String FXML = "EquipmentListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(EquipmentListPanel.class);

    @FXML
    private ListView<Equipment> equipmentListView;

    /** Creates an equipment panel backed by the model's observable list. */
    public EquipmentListPanel(ObservableList<Equipment> equipmentList) {
        super(FXML);
        equipmentListView.setItems(equipmentList);
        equipmentListView.setCellFactory(listView -> new EquipmentListViewCell());
    }

    private static class EquipmentListViewCell extends ListCell<Equipment> {
        @Override
        protected void updateItem(Equipment equipment, boolean empty) {
            super.updateItem(equipment, empty);
            if (empty || equipment == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            Label id = new Label(equipment.getId().value);
            id.getStyleClass().add("cell_small_label");
            Label name = new Label(equipment.getName().value);
            name.getStyleClass().add("cell_big_label");
            VBox details = new VBox(4, id, name);
            details.setMinHeight(64);
            HBox card = new HBox(details);
            card.setStyle("-fx-padding: 8 12 8 12;");
            setGraphic(card);
        }
    }
}
