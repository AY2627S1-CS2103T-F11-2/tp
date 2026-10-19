package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentStatus;

/** Displays the ID, name and color-coded status of an equipment item. */
public class EquipmentCard extends UiPart<Region> {
    private static final String FXML = "EquipmentListCard.fxml";
    private static final String STYLE_AVAILABLE = "status-available";
    private static final String STYLE_ISSUED = "status-issued";

    @FXML
    private Label equipmentId;
    @FXML
    private Label name;
    @FXML
    private Label borrower;
    @FXML
    private Circle statusIndicator;

    /** Creates a card for the given equipment item. */
    public EquipmentCard(Equipment equipment) {
        super(FXML);
        equipmentId.setText(equipment.getId().toString());
        name.setText(equipment.getName().toString());
        borrower.setText(equipment.getDisplayStatus());
        statusIndicator.getStyleClass().add(getStatusStyleClass(equipment.getStatus()));
    }

    /** Returns the style class that colors the status indicator for {@code status}. */
    private static String getStatusStyleClass(EquipmentStatus status) {
        return switch (status) {
            case AVAILABLE -> STYLE_AVAILABLE;
            case ISSUED -> STYLE_ISSUED;
        };
    }
}
