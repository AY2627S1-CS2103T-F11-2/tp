package seedu.address.testutil;

import seedu.address.model.ClubLogisticsData;
import seedu.address.model.person.Person;

/**
 * A utility class to help with building ClubLogisticsData objects.
 * Example usage: <br>
 *     {@code ClubLogisticsData data = new ClubLogisticsDataBuilder().withPerson(person).build();}
 */
public class ClubLogisticsDataBuilder {

    private ClubLogisticsData clubLogisticsData;

    /** Starts building an empty data set. */
    public ClubLogisticsDataBuilder() {
        clubLogisticsData = new ClubLogisticsData();
    }

    /** Starts building from the supplied data set. */
    public ClubLogisticsDataBuilder(ClubLogisticsData clubLogisticsData) {
        this.clubLogisticsData = clubLogisticsData;
    }

    /**
     * Adds a new {@code Person} to the {@code ClubLogisticsData} that we are building.
     */
    public ClubLogisticsDataBuilder withPerson(Person person) {
        clubLogisticsData.addPerson(person);
        return this;
    }

    /** Returns the data set being built. */
    public ClubLogisticsData build() {
        return clubLogisticsData;
    }
}
