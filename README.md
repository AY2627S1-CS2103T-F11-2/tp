[![Java CI](https://github.com/AY2627S1-CS2103T-F11-2/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-F11-2/tp/actions)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-F11-2/tp/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103T-F11-2/tp)

![Ui](docs/images/Ui.png)

# ClubLogistics

* ClubLogistics is a **desktop application for student club logistics coordinators** to track reusable club equipment and see who each item is currently issued to.
* It is optimised for **CLI users**: all actions are performed by typing commands, so an experienced typist can update the equipment register faster than with a mouse-driven app.
* Main features:
  * `add <equipment-id> <equipment-name>` — register a new piece of equipment
  * `remove <equipment-id>` — delete an equipment record
  * `list` — show all equipment, their status (Available / Issued) and current holder
  * `issue <equipment-id> <person-name>` — record that equipment has been issued to a person
  * `return <equipment-id>` — record that issued equipment has been returned
* For the detailed documentation of this project, see the **[ClubLogistics Product Website](https://ay2627s1-cs2103t-f11-2.github.io/tp/)**.

## Acknowledgements

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
