---
layout: page
title: Developer Guide
---

* Table of Contents
{:toc}

## **Acknowledgements**

ClubLogistics builds on the [SE-EDU AddressBook Level 3](https://github.com/se-edu/addressbook-level3)
application framework. It uses JavaFX for the desktop interface, Jackson for JSON storage, JUnit 5 for tests,
and Gradle for builds.

## **Setting up, getting started**

Refer to [Setting up and getting started](SettingUp.md). Build and run the project with Java 25.

## **Design**

### Architecture

**Main components**

`Main` launches `MainApp`, which manages application startup and shutdown:

* On startup, it creates and connects the storage, model, logic, and UI components and loads preferences
  and equipment data. Missing equipment data uses sample data; invalid equipment data uses an empty register.
* On shutdown, it saves the user preferences, including window settings.

The four main components are:

* [**`UI`**](#ui-component): Displays equipment and handles user input.
* [**`Logic`**](#logic-component): Parses and executes commands.
* [**`Model`**](#model-component): Holds equipment data and user preferences in memory.
* [**`Storage`**](#storage-component): Reads and writes equipment data and preferences.

[**`Commons`**](#common-classes) contains utilities shared by multiple components.

**Component interactions**

The sequence diagram below shows the interactions for a successful `remove CAM001` command, assuming
`CAM001` is registered and available. Parsing, equipment lookup, and availability checks are omitted.

![Successful remove CAM001 interactions between UI, Logic, Model, and Storage](images/ArchitectureSequenceDiagram.png)

Each component exposes its API through an interface (`Ui`, `Logic`, `Model`, or `Storage`) and implements it
in a corresponding manager class (`UiManager`, `LogicManager`, `ModelManager`, or `StorageManager`).

For example, `Logic.java` declares the command API and `LogicManager.java` implements it. Components depend
on these interfaces rather than specific implementations. The partial class diagram below shows this
relationship for `LogicManager`, `Model`, and `Storage`.

![Component interfaces and their manager implementations](images/ComponentManagers.png)

### UI component

`UiManager` creates `MainWindow`. The window contains `CommandBox`, `ResultDisplay`, `EquipmentListPanel`,
and `StatusBarFooter`. `EquipmentListPanel` displays `EquipmentCard` instances bound to the model's filtered
observable equipment list. Cards show each item's ID, name, and availability or borrower. `HelpWindow`
displays the ClubLogistics User Guide link. Visible components inherit from `UiPart` and use matching FXML layouts.

### Logic component

`LogicManager.execute` passes input to `ClubLogisticsParser`, executes the resulting command against `Model`,
and saves the register after successful execution. Parse and command errors are reported to the user.
Storage errors are converted to command errors, with a separate message for permission failures.

The supported commands are `add`, `remove`, `list`, `filter`, `issue`, `return`, `clear`, `help`, and `exit`.
Each equipment command parser validates its own positional arguments. For example, `issue CAM001 John Tan`
creates an `IssueCommand` containing an `EquipmentId` and a `BorrowerName`.

#### Positional command parsers

The following classes are used for parsing user commands:

![Parser structure using XYZ placeholders for the concrete parser and command pairs](images/ParserClasses.png)

How the parsing works:

* `ClubLogisticsParser` creates the matching `XYZCommandParser`, which parses the arguments and creates an
  `XYZCommand`. The resulting command is returned to `LogicManager` for execution.
* All `XYZCommandParser` classes implement `Parser<T extends Command>`.

`XYZCommandParser` and `XYZCommand` are placeholders for the following concrete classes:

| Command word | `XYZCommandParser` represents | `XYZCommand` represents |
| --- | --- | --- |
| `add` | `AddEquipmentCommandParser` | `AddEquipmentCommand` |
| `remove` | `RemoveCommandParser` | `RemoveCommand` |
| `list` | `ListCommandParser` | `ListCommand` |
| `filter` | `FilterCommandParser` | `FilterCommand` |
| `issue` | `IssueCommandParser` | `IssueCommand` |
| `return` | `ReturnCommandParser` | `ReturnCommand` |
| `clear` | `ClearCommandParser` | `ClearCommand` |
| `help` | `HelpCommandParser` | `HelpCommand` |
| `exit` | `ExitCommandParser` | `ExitCommand` |

### Model component

`ModelManager` owns a copy of `ClubLogisticsData`, user preferences, and a `FilteredList<Equipment>`.
`ClubLogisticsData` uses `UniqueEquipmentList` to enforce case-insensitive ID uniqueness and exposes an
unmodifiable observable equipment list. Replacing the register validates all records before changing the live list.

`Equipment` is immutable. Its `EquipmentId`, `EquipmentName`, and optional `BorrowerName` describe one physical
item. Equipment is available when no borrower is recorded and issued otherwise. Issuing and returning replace
the item, so observers receive the updated record without losing the selected filter.

### Storage component

`StorageManager` delegates to `JsonUserPrefsStorage` and `JsonClubLogisticsStorage`. The latter reads and writes
`JsonSerializableClubLogisticsData`, whose top-level `equipment` array contains `JsonAdaptedEquipment` records.
Each record stores `id`, `name`, `status`, and `issuedTo`. `status` must be `AVAILABLE` with a null or empty
`issuedTo`, or `ISSUED` with a valid borrower; any other combination causes loading to fail. A record without
`status` is accepted for compatibility with older files, and its status is inferred from the borrower. Saving always
writes `status`. Invalid IDs, names, borrowers, statuses, duplicate IDs, and null records also cause loading to fail.
No contact register is stored.

The application uses `data/clublogistics.json` exclusively. No data migration is performed.

### Common classes

The `commons` package provides logging, file and JSON utilities, argument checks, and window settings.
`LogsCenter` writes `clublogistics.log` files and uses the `clublogistics` logger hierarchy.

## **Implementation**

### Equipment lifecycle

`AddEquipmentCommand` rejects duplicate IDs and creates available equipment. `IssueCommand` requires an
available item and records its borrower. `ReturnCommand` requires an issued item and clears the borrower.
`RemoveCommand` rejects issued equipment. Each command validates its preconditions before changing the model.
`ClearCommand` resets the entire register.

### Filtering

`FilterCommand` selects available or issued equipment. The same observable filtered list stays bound to the UI
as items are added, removed, issued, or returned. `ListCommand` restores the predicate that shows every item.
The filter is a view preference for the running session and is not saved in the equipment file.

### Existing diagrams

The remaining diagrams below and their sources in `docs/diagrams` have not yet been adapted to the
equipment-only implementation; use the descriptions above and the source code for the current design.

<img src="images/ArchitectureDiagram.png" width="280" />

![Structure of the UI Component](images/UiClassDiagram.png)

<img src="images/LogicClassDiagram.png" width="550"/>

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<img src="images/ModelClassDiagram.png" width="450" />

<img src="images/BetterModelClassDiagram.png" width="450" />

<img src="images/StorageClassDiagram.png" width="550" />

![UndoRedoState0](images/UndoRedoState0.png)

![UndoRedoState1](images/UndoRedoState1.png)

![UndoRedoState2](images/UndoRedoState2.png)

![UndoRedoState3](images/UndoRedoState3.png)

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

![UndoRedoState4](images/UndoRedoState4.png)

![UndoRedoState5](images/UndoRedoState5.png)

<img src="images/CommitActivityDiagram.png" width="250" />

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

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

### Launch and shutdown

1. Build `clublogistics.jar` with `./gradlew shadowJar` using Java 25 and copy it into an empty folder.
2. Run `java -jar clublogistics.jar`. Expect available sample equipment `CAM001`, `MIC001`, and `PROJ001`.
3. Resize and move the window, exit, and relaunch. Expect the window size and position to be retained.

### Equipment lifecycle and filtering

1. Run `add TRI001 Tripod`, then `filter available`. Expect the tripod to appear as available.
2. Run `issue TRI001 John Tan`. Expect it to disappear from the available view.
3. Run `filter issued`. Expect the tripod to show `Issued to: John Tan`.
4. Run `remove TRI001`. Expect an error and no changes because the item is issued.
5. Run `return TRI001`, then `list`. Expect the tripod to be available.
6. Run `remove tri001`. Expect only the tripod to be removed, regardless of ID capitalization.
7. Run `clear`. Expect an empty equipment register.

### Saving data and reporting errors

1. Add equipment, issue it, exit, and relaunch. Expect the item and borrower to be retained.
2. With the app closed, check `data/clublogistics.json`. Expect only an `equipment` array at the top level.
3. In a disposable test folder, replace the data file with invalid JSON and launch the app. Expect an empty register.
   The invalid file is left on disk until a successful command saves the current register.
4. Run `help`. Expect the ClubLogistics User Guide link.
5. Try an invalid equipment ID or an unknown command. Expect an error without changing any records.
