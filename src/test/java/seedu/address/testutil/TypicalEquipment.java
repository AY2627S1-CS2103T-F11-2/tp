package seedu.address.testutil;

import seedu.address.model.ClubLogisticsData;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/** Equipment fixtures covering both availability states. */
public class TypicalEquipment {
    public static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    public static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone",
            new BorrowerName("John Tan"));
    public static final Equipment PROJECTOR = new Equipment(new EquipmentId("PROJ001"), "Epson Projector");

    /** Returns a fresh register with available and issued equipment. */
    public static ClubLogisticsData getTypicalClubLogisticsData() {
        ClubLogisticsData data = new ClubLogisticsData();
        data.addEquipment(CAMERA);
        data.addEquipment(MICROPHONE);
        return data;
    }
}
