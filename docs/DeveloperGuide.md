---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

ClubLogistics is intended for a **student-club logistics coordinator** who:

* is the single person responsible for maintaining the club's equipment register.
* tracks individual reusable assets, such as speakers, microphones, cameras, projectors, and extension reels.
* needs to know what equipment exists, where it is stored, whether it is usable and available, and who is responsible for borrowed equipment.
* performs many short updates and retrieves records quickly, especially while preparing for events, issuing or receiving equipment, conducting stocktakes, and handing over the role.
* can type quickly, prefers keyboard-driven workflows, and is comfortable entering text commands in a desktop application.

ClubLogistics does not target the management of consumables or bulk stock. It also excludes member accounts, concurrent editing, cloud synchronisation, remote servers, and barcode or QR-code scanning as a required workflow.

**Value proposition**: ClubLogistics keeps a student club's equipment records organised and quick to retrieve through a typing-first workflow, reducing misplaced equipment, missed returns, and uncertainty about availability and condition.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`.

The stories below describe the full product vision. They include deferred capabilities that might not be implemented in the final course release.

| ID | Priority | As a …​ | I want to …​ | So that I can …​ |
| -- | -------- | ------- | ------------- | ----------------- |
| US01 | `* * *` | logistics coordinator | add a reusable asset with enough information to identify, locate, and assess it | record new equipment consistently |
| US02 | `* * *` | logistics coordinator | view all recorded assets | understand what equipment the club owns |
| US03 | `* *` | logistics coordinator | view the details of one asset | check its current state before acting on it |
| US04 | `* * *` | logistics coordinator | find an asset by its ID or name | retrieve its record quickly during a handover or loan |
| US05 | `* *` | logistics coordinator | filter assets by category | focus on the type of equipment needed for an event |
| US06 | `* *` | logistics coordinator | filter assets by availability | see which equipment can be issued now |
| US07 | `* *` | logistics coordinator | change an asset's storage location | keep the register accurate when equipment is moved |
| US08 | `* *` | logistics coordinator | correct an asset's recorded details | prevent typing mistakes from remaining in the register |
| US09 | `* * *` | logistics coordinator | remove an asset that was entered by mistake | prevent an incorrect record from cluttering the register |
| US10 | `*` | logistics coordinator | archive an asset that has been disposed of | retain its history without showing it as usable |
| US11 | `* * *` | logistics coordinator | record a loan with a borrower and due date | know who is responsible for an asset and when it should return |
| US12 | `* * *` | logistics coordinator | view all active loans | monitor equipment that is outside storage |
| US13 | `* * *` | logistics coordinator | record the return of a loaned asset | make the asset available again |
| US14 | `* *` | logistics coordinator | view overdue loans | follow up on late returns first |
| US15 | `* *` | logistics coordinator | find active loans by borrower name | handle a borrower's return without checking every loan |
| US16 | `* *` | logistics coordinator | extend an active loan's due date | reflect an approved extension accurately |
| US17 | `* *` | logistics coordinator | cancel a loan recorded by mistake | avoid making an available asset appear unavailable |
| US18 | `*` | logistics coordinator | view an asset's previous loans | investigate repeated loss, damage, or late returns |
| US19 | `*` | logistics coordinator | view a borrower's previous loans | understand their borrowing history before issuing valuable equipment |
| US20 | `* *` | logistics coordinator | add a short purpose or event note to a loan | remember why the equipment was issued |
| US21 | `* *` | logistics coordinator | record an asset's condition when adding it | establish the equipment's starting condition |
| US22 | `* *` | logistics coordinator | record an asset's condition when it is returned | avoid overlooking damage discovered during return |
| US23 | `* * *` | logistics coordinator | mark an asset as damaged | prevent unsafe or unusable equipment from being issued |
| US24 | `* *` | logistics coordinator | list all damaged assets | plan repairs and replacements |
| US25 | `* *` | logistics coordinator | add a note describing reported damage | make the repair issue clear to whoever handles it later |
| US26 | `* *` | logistics coordinator | mark a damaged asset as usable after repair | return repaired equipment to the available pool |
| US27 | `* *` | logistics coordinator | record that an asset is missing | avoid promising equipment that cannot be found |
| US28 | `* *` | logistics coordinator | record a maintenance due date | arrange servicing before equipment fails during an event |
| US29 | `* *` | logistics coordinator | view counts of available, loaned, damaged, and missing assets | assess equipment readiness quickly |
| US30 | `* *` | logistics coordinator | sort assets by name, category, location, or condition | review a large register in a useful order |
| US31 | `* *` | logistics coordinator | view assets stored at a particular location | prepare equipment without searching every storage area |
| US32 | `*` | logistics coordinator | tag assets for an activity or event type | identify suitable equipment even when categories differ |
| US33 | `* *` | logistics coordinator | add private handling or setup notes to an asset | keep important operational details with its record |
| US34 | `*` | logistics coordinator | view recent changes to assets and loans | understand what happened since the last inventory check |
| US35 | `*` | logistics coordinator | export a human-readable inventory list | conduct a physical stocktake or share a handover snapshot |
| US36 | `* *` | logistics coordinator | export active and overdue loans | use the list for follow-up and committee handover |
| US37 | `*` | logistics coordinator | back up the club's equipment records | avoid losing the register if the device or file is lost |
| US38 | `*` | logistics coordinator | restore records from a backup | recover the register after data loss |
| US39 | `* *` | new logistics coordinator | review unresolved loans, missing assets, and damaged assets | take over the role without overlooking existing problems |
| US40 | `* *` | logistics coordinator | see when each asset record was last updated | identify information that may be stale |

### Use cases

(For all use cases below, the **System** is `ClubLogistics` and the **Actor** is the logistics coordinator.)

#### Use case UC01: Register a new asset

**MSS**

1. Coordinator requests to add an asset and provides its identifying details, storage location, and condition.
2. ClubLogistics validates the supplied details and checks that the equipment ID is unique.
3. ClubLogistics records the asset as available.
4. ClubLogistics displays the new asset record.

    Use case ends.

**Extensions**

* 2a. The equipment ID or another required field is invalid.
  * 2a1. ClubLogistics shows the invalid field and its requirements.
  * 2a2. Coordinator corrects the details.
  * Steps 2-4 are repeated.
* 2b. An asset with the same equipment ID already exists.
  * 2b1. ClubLogistics rejects the request and displays the existing asset.

    Use case ends.

#### Use case UC02: Issue an asset to a borrower

**Preconditions**: The asset exists and is available and usable.

**MSS**

1. Coordinator searches for the asset by equipment ID or name.
2. ClubLogistics displays matching assets and their availability and condition.
3. Coordinator selects an asset and provides the borrower's details, due date, and an optional purpose.
4. ClubLogistics validates the loan details.
5. ClubLogistics creates an active loan and marks the asset as loaned.
6. ClubLogistics displays the recorded borrower and due date.

    Use case ends.

**Extensions**

* 2a. No matching asset exists.
  * 2a1. ClubLogistics informs the coordinator that no asset was found.

    Use case ends.
* 4a. The asset is already loaned, damaged, missing, or archived.
  * 4a1. ClubLogistics rejects the loan and displays why the asset cannot be issued.

    Use case ends.
* 4b. The borrower details or due date are invalid.
  * 4b1. ClubLogistics shows the invalid field and its requirements.
  * 4b2. Coordinator corrects the loan details.
  * Steps 4-6 are repeated.

#### Use case UC03: Receive an asset and record damage

**Preconditions**: The asset has an active loan.

**MSS**

1. Coordinator finds the active loan by equipment ID or borrower name.
2. ClubLogistics displays the matching active loan.
3. Coordinator records the return and the asset's condition.
4. ClubLogistics closes the loan and marks the asset as available.
5. ClubLogistics displays the updated asset and loan records.

    Use case ends.

**Extensions**

* 2a. No active loan matches the search.
  * 2a1. ClubLogistics informs the coordinator that no active loan was found.

    Use case ends.
* 3a. The asset is damaged.
  * 3a1. Coordinator records a damage description.
  * 3a2. ClubLogistics closes the loan, marks the asset as damaged and unavailable, and saves the description.
  * Use case resumes at step 5.

#### Use case UC04: Prepare equipment for an event

**MSS**

1. Coordinator requests assets for a category, event tag, or storage location.
2. ClubLogistics displays the matching assets with their availability and condition.
3. Coordinator filters the results to assets that are available and usable.
4. ClubLogistics displays the suitable assets and a count of the results.
5. Coordinator views a selected asset's details to confirm its location and handling notes.
6. ClubLogistics displays the selected asset's full record.

    Use case ends.

**Extensions**

* 4a. No suitable asset is available.
  * 4a1. Coordinator requests matching assets that are loaned, damaged, or missing.
  * 4a2. ClubLogistics displays their state and, where applicable, borrower and due-date information.

    Use case ends.

#### Use case UC05: Hand over the logistics role

**MSS**

1. Coordinator requests a summary of unresolved loans, damaged assets, and missing assets.
2. ClubLogistics displays the outstanding items and recent changes.
3. Coordinator requests exports of the inventory and active or overdue loans.
4. ClubLogistics creates human-readable export files.
5. Coordinator creates a backup of the equipment records.
6. ClubLogistics confirms that the backup was created successfully.

    Use case ends.

**Extensions**

* 4a. An export cannot be written to the selected location.
  * 4a1. ClubLogistics reports the error without changing any records.
  * 4a2. Coordinator selects another writable location.
  * Steps 4-6 are repeated.

### Non-Functional Requirements

1. **NFR01 - Platform independence:** ClubLogistics should work on Windows, Linux, and macOS on a computer with Java `25` installed.
2. **NFR02 - Portability:** ClubLogistics should run without an installer and should be distributed as a single JAR file no larger than 100 MB.
3. **NFR03 - Single-user operation:** ClubLogistics should support one logistics coordinator and should not require user accounts, concurrent editing, or shared access to its live data file.
4. **NFR04 - Local and durable storage:** ClubLogistics should store all operational data locally in a human-editable text file, should not require a DBMS or remote server, and should retain successful changes after the application is closed and restarted.
5. **NFR05 - Offline availability:** All core asset, loan, condition, and search functions should remain usable without an Internet connection or third-party account.
6. **NFR06 - Performance:** With up to 1,000 asset records and 5,000 loan-history records, ClubLogistics should complete common commands such as add, find, list, issue, and return within one second on a typical modern laptop, excluding application start-up and file export time.
7. **NFR07 - Typing-first usability:** Every product function should be accessible using text commands. A user with above-average typing speed should be able to complete common workflows faster than with an equivalent mouse-only workflow.
8. **NFR08 - Learnability and feedback:** Command formats and validation errors should state what input is required, and every command should provide a clear success or failure message without silently changing data.
9. **NFR09 - Data integrity:** Equipment IDs should uniquely identify physical assets regardless of letter case. ClubLogistics should reject operations that would create contradictory records, such as issuing an unavailable asset, returning an available asset, or removing an asset with an active loan.
10. **NFR10 - Screen compatibility:** The GUI should work well at resolutions of 1920x1080 and above at 100% and 125% scaling, and remain fully usable at resolutions of 1280x720 and above at 150% scaling.
11. **NFR11 - Privacy:** Borrower details and private asset notes should remain on the user's device unless the user explicitly exports or copies them.
12. **NFR12 - Recoverability:** A failed command, failed export, or interrupted save should not partially apply an operation or corrupt the last valid stored data.

### Glossary

* **Active loan**: A loan that has been issued and has not yet been returned or cancelled.
* **Archived asset**: An asset retained for historical reference but excluded from the usable equipment register.
* **Asset**: One individually tracked, reusable piece of club equipment. Each physical item is a separate asset even when several items have the same name.
* **Available asset**: An asset that is usable, not archived, not missing, and not part of an active loan.
* **Borrower**: The person currently responsible for an asset that has been issued.
* **Category**: A broad classification of an asset by equipment type, such as audio, video, or power.
* **CLI (Command Line Interface)**: A text-command interface through which the user invokes ClubLogistics functions.
* **Condition**: The recorded physical or operational state of an asset, such as usable or damaged.
* **Due date**: The date by which an active loan is expected to be returned.
* **Equipment ID**: A case-insensitive, unique identifier assigned to one physical asset.
* **Equipment register**: The complete collection of asset records maintained by ClubLogistics.
* **Event tag**: A user-defined label that groups assets suitable for an activity or event type independently of category.
* **Loan**: A record that an asset was issued to a borrower, including its issue status and expected return date.
* **Logistics coordinator**: The single user responsible for maintaining the club's equipment register.
* **Missing asset**: An asset whose current physical location is unknown and which cannot be issued.
* **Overdue loan**: An active loan whose due date has passed.
* **Private note**: Handling, setup, or other operational information intended only for the logistics coordinator.
* **Stocktake**: A physical check of recorded assets against the equipment actually present.
* **Storage location**: The place where an asset is normally kept when it is not on loan.
* **Usable asset**: An asset whose recorded condition permits it to be issued; it may still be unavailable because it is currently on loan.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
