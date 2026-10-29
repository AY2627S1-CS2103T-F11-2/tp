package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.parser.ClubLogisticsParser;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

/** Exercises borrower search and the persistent view description through the real parser. */
public class BorrowerSearchTest {
    /** Matches a normalized substring without regard to capitalization and excludes available items. */
    @Test
    public void find_matchesBorrowersAndPersistsAcrossChanges() throws Exception {
        ModelManager model = new ModelManager();
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Camera", new BorrowerName("Alice Lim"));
        Equipment mic = new Equipment(new EquipmentId("MIC001"), "Microphone", new BorrowerName("John Tan"));
        model.addEquipment(camera);
        model.addEquipment(mic);
        ClubLogisticsParser parser = new ClubLogisticsParser();
        assertTrue(parser.parseCommand("find   aLiCe    lI  ").execute(model)
                .getFeedbackToUser().contains("Found 1 equipment item for borrower:"));
        assertEquals(List.of(camera), model.getFilteredEquipmentList());
        assertTrue(model.equipmentFilterDescriptionProperty().get().contains("aLiCe lI"));
        model.addEquipment(new Equipment(new EquipmentId("TRI001"), "Tripod"));
        assertEquals(List.of(camera), model.getFilteredEquipmentList());
        parser.parseCommand("return CAM001").execute(model);
        assertTrue(model.getFilteredEquipmentList().isEmpty());
        parser.parseCommand("list").execute(model);
        assertEquals(3, model.getFilteredEquipmentList().size());
        assertEquals("Showing all equipment", model.equipmentFilterDescriptionProperty().get());
    }

    /** Retains the current view on invalid input and reports when nobody matches. */
    @Test
    public void find_rejectsInvalidQueryAndHandlesNoMatches() throws Exception {
        ModelManager model = new ModelManager();
        ClubLogisticsParser parser = new ClubLogisticsParser();
        parser.parseCommand("filter issued").execute(model);
        String before = model.equipmentFilterDescriptionProperty().get();
        for (String query : List.of("find", "find 123", "find Alice!", "find " + "a".repeat(51))) {
            assertThrows(ParseException.class, () -> parser.parseCommand(query));
            assertEquals(before, model.equipmentFilterDescriptionProperty().get());
        }
        assertTrue(parser.parseCommand("find Nobody").execute(model).getFeedbackToUser().contains("No equipment"));
        assertTrue(model.equipmentFilterDescriptionProperty().get().contains("Nobody"));
    }

    /** The status indicator remains meaningful after adding an item that the view excludes. */
    @Test
    public void filter_addExcludedItem_retainsDescription() throws Exception {
        ModelManager model = new ModelManager();
        ClubLogisticsParser parser = new ClubLogisticsParser();
        parser.parseCommand("filter issued").execute(model);
        parser.parseCommand("add TRI001 Tripod").execute(model);
        assertTrue(model.getFilteredEquipmentList().isEmpty());
        assertTrue(model.equipmentFilterDescriptionProperty().get().contains("Issued"));
        parser.parseCommand("filter available").execute(model);
        assertTrue(model.equipmentFilterDescriptionProperty().get().contains("Available"));
    }
    /** Returns every matching loan in register order and refreshes after a new matching issue. */
    @Test
    public void find_multipleLoansAndNewIssue_updatesAllMatches() throws Exception {
        ModelManager model = new ModelManager();
        ClubLogisticsParser parser = new ClubLogisticsParser();
        parser.parseCommand("add CAM001 Camera").execute(model);
        parser.parseCommand("add MIC001 Microphone").execute(model);
        parser.parseCommand("add TRI001 Tripod").execute(model);
        parser.parseCommand("issue CAM001 Alice Lim").execute(model);
        parser.parseCommand("issue MIC001 Alice Tan").execute(model);
        assertTrue(parser.parseCommand("find ALICE").execute(model)
                .getFeedbackToUser().contains("Found 2 equipment items for borrower:"));
        assertEquals(2, model.getFilteredEquipmentList().size());
        parser.parseCommand("issue TRI001 Alice Lim").execute(model);
        assertEquals(3, model.getFilteredEquipmentList().size());
        assertEquals("CAM001", model.getFilteredEquipmentList().getFirst().getId().toString());
        parser.parseCommand("filter available").execute(model);
        assertTrue(model.getFilteredEquipmentList().isEmpty());
    }

}
