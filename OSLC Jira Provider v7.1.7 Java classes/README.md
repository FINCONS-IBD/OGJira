# OSLC Jira Plugin classes  

This repository contains the Java classes that replaces the homonimous classes in the source project:  
* package com.ericsson.jira.oslc.resources: JiraChangeRequest, JiraIssueType, JiraIssueStatus.  
* package com.ericsson.jira.oslc.services: IssueStatusService.  

The oslcjira7 plugin Java Project is available at this link: https://github.com/Ericsson/jira-oslc-plugin/tree/jira7.  

## .jar Derivation  

For the derivation of the .jar file, the Atlassian-SDK is needed. You should perform a Maven Build of the project using the ".setting" file located in the Maven folder of Atlassian-SDK.