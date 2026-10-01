# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Maintain JUnit test coverage for approximately the top 50% highest-value methods. Prioritize complex methods, core behavior, and critical business logic rather than selecting methods solely to increase the raw coverage count.

After every code change, review the affected behavior and update the JUnit tests as needed to continue meeting this target. Run the relevant Gradle tests with Java 25 to verify the changes.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Git commit message style:

Use the following structure for git messages:

{current situation} -- use present tense

{why it needs to change} -- explain rationale behind the commit

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}


Do not use bullet points. Split into paragraphs when applicable.

You may add a <scope>: or <category>: in front, when applicable.
The subject after the colon should still start with an uppercase letter.
Use DG and UG as abbreviations for Developer Guide and User Guide only in the subject to reduce sentence length.
e.g. Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date

The word Let's should be used to indicate the beginning of the section that describes the change done in the commit.

The commit message should also follow the following guidelines:
- Separate subject from body with a blank line.
- Wrap the body at 72 characters.
- Use blank lines to separate paragraphs.

Use the following example of git message as a guide:

Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code duplication.

Extracting the common behavior into a super class allows us to use
polymorphism when dealing with person attributes. For example, validity
checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.
