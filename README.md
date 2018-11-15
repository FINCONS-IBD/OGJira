# OGJira

[![DepShield Badge](https://depshield.sonatype.org/badges/FINCONS-IBD/OGJira/depshield.svg)](https://depshield.github.io)

The **OGJira System** has been specifically designed and developed to address the needs of companies that use Microsoft Excel for requirements management, but want to have it capable to interact via OSLC with other tools used in the software development process like a tracker system used to handle issues such as requests for support, bugs, defects, or change requests.
Indeed, Microsoft Excel is often used to manage requirements due to its ability to easily structure data elements, to filter data, to provide formulas and macros. Often, Excel is used in conjunction with Microsoft SharePoint that provides Excel files’ sharing and version control, track changes, etc.
Of course, Excel does not natively support integration with other software development related tools and is not aware of standards like OSLC.
OGJira tool comprises a set of Excel macros able to interact with the Jira issue tacking system using OSLC compliant messages.
The OGJira system is made up of the following components:
- a set of Excel macros able to extend MS Excel enabling it to act as an OSLC consumer able to use the Event Bus to publish requirement’s change related events;
- additional Excel macros to support the user in managing requirements (e.g., retrieve from Jira all tickets related to a specific requirement) or to directly create a new ticket on Jira from within the Excel tool;
- the CEP Based Events’ Subscriber that is a Java module acting as a subscriber towards the Event Bus so that it receives all events it has subscribed to (i.e., all requirement’s change request published by an OGJira Excel instance) and embedding a PROTON CEP filtering component so that only events that meet a specific pattern are actually processed. The processing performed on the survived filtered events is to reopen all Jira tickets whose requirements have been changed.
- A Jira OSLC provider, which is a customized version of the Jira OSLC plugin (https://github.com/Ericsson/jira-oslc-plugin ), based on the Eclipse Lyo project (http://www.eclipse.org/lyo/ ). This component is able to expose Jira objects as OSLC resources.

## Main Functionalities

The OGJira Excel extension provides the following features:
- User authentication: when starting an OGJira extended Excel file, the user is requested to provide his/her Jira credentials so that the OGJira Excel macros can operate (e.g., retrieve tickets, create new tickets) on the Jira system via the OSLC Jira plugin;
- Possibility to get a list of bugs or create a new one. 
- Management of requirements. The OGJira Excel extension provides a set of fields where to specify information (e.g., Requirement Title, Requirement Description) or select from a predefined set of values (e.g., Requirement Type, Status). Additionally, for each requirement the user has two buttons “Modified” and “Find”. The first button must be used to actually generate a change request event to notify, via the Event Bus, all interested services that the requirement at hand has been changed. The “Find” button, instead, can be used to get a list of tickets associated to the requirement at hand.

The OGJira CEP Based Events’ Subscriber is essentially a RabbitMQ subscriber to receive relevant published events that uses the CEP PROTON to filter received events according to predefined patterns.
The OSLC Jira plugin exposes Jira resources as OSLC ones.
