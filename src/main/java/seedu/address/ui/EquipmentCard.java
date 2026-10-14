package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.equipment.Equipment;

/** Displays the ID, name and status of an equipment item. */
public class EquipmentCard extends UiPart<Region> {
    private static final String FXML = "EquipmentListCard.fxml";

    @FXML
    private Label equipmentId;
    @FXML
    private Label name;
    @FXML
    private Label status;

    @FXML
    private Label borrower;

    /** Creates a card for the given equipment item. */
    public EquipmentCard(Equipment equipment) {
        super(FXML);
        equipmentId.setText(equipment.getId().toString());
        name.setText(equipment.getName().toString());
        status.setText("Status: " + equipment.getStatus());
        borrower.setText(equipment.getDisplayStatus());
        borrower.setVisible(equipment.isIssued());
        borrower.setManaged(equipment.isIssued());
    }
}
