package seedu.address.model.util;

import seedu.address.model.ClubLogisticsData;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Contains utility methods for populating {@code ClubLogisticsData} with sample data.
 */
public class SampleDataUtil {
    /** Returns sample equipment for a fresh installation. */
    public static ReadOnlyClubLogisticsData getSampleClubLogisticsData() {
        ClubLogisticsData sampleData = new ClubLogisticsData();
        for (Equipment sampleEquipment : getSampleEquipment()) {
            sampleData.addEquipment(sampleEquipment);
        }
        return sampleData;
    }

    /**
     * Returns available sample equipment for trying the issue command on a fresh installation.
     */
    public static Equipment[] getSampleEquipment() {
        return new Equipment[] {
            new Equipment(new EquipmentId("CAM001"), "Sony Camera"),
            new Equipment(new EquipmentId("MIC001"), "Wireless Microphone"),
            new Equipment(new EquipmentId("PROJ001"), "Epson Projector")
        };
    }

}
