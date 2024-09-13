---
layout: home
permalink: index.html

# Please update this with your repository name and title
repository-name: e18-4yp-Extending-and-Implementing-Process-Mining-Techniques-Prevention-and-Mitigation
title: Extending and Implementing Process Mining Techniques Improving Data Quality - Prevention and Mitigation
---

[comment]: # "This is the standard layout for the project, but you can clean this and use your own template"

# Extending and Implementing Process Mining Techniques Improving Data Quality - Prevention and Mitigation

#### Team

- E/18/010, Abeywickrama A.K.D.A.S., [email](mailto:e18010@eng.pdn.ac.lk)
- E/18/156, Jayathilake W.A.T.N., [email](mailto:e18156@eng.pdn.ac.lk)
- E/18/329, Sewwandi D.W.S.N., [email](mailto:e18329@eng.pdn.ac.lk)

#### Supervisors
 
- Prof. Roshan G. Ragel, [email](mailto:roshanr@eng.pdn.ac.lk)
- Dr. Asitha Bandaranayake, [email](mailto:asithab@eng.pdn.ac.lk)
- Dr. Damayanthi Herath, [email](mailto:damayanthiherath@eng.pdn.ac.lk)
- Prof. Athur ter Hofstede, [email](mailto:a.terhofstede@qut.edu.au)
- Dr. Chathura Ekanayake, [email](mailto:chathura@wso2.com)

#### Table of content

1. [Abstract](#abstract)
2. [Related works](#related-works)
3. [Methodology](#methodology)
4. [Experiment Setup and Implementation](#experiment-setup-and-implementation)
5. [Results and Analysis](#results-and-analysis)
6. [Conclusion](#conclusion)
7. [Publications](#publications)
8. [Links](#links)

---

<!-- 
DELETE THIS SAMPLE before publishing to GitHub Pages !!!
This is a sample image, to show how to add images to your page. To learn more options, please refer [this](https://projects.ce.pdn.ac.lk/docs/faq/how-to-add-an-image/)
![Sample Image](./images/sample.png) 
-->


## Abstract

Process mining has become a valuable tool for analyzing business processes. However, the quality of the data used, specifically event logs, significantly impacts the accuracy of the results. This project addresses the challenge of process-data quality by focusing on prevention and mitigation strategies. We leverage the Odigos framework to identify root causes of data quality issues within event logs. Additionally, we contribute to the PraeclarusPDQ framework by developing a software plug-in to analyze these root causes for specific event log imperfection patterns.

## Background

Process mining has emerged as a powerful tool for analyzing and improving business processes by extracting insights from event logs. These logs record process execution information, serving as the foundation for discovering process models, checking conformance, and enhancing operational efficiency. However, the quality of event logs significantly impacts the reliability of process mining results.

Recent research has identified various event log imperfection patterns that hinder effective process mining. Suriadi et al.'s work on "Event log imperfection patterns for process mining" categorized common data quality issues encountered in practice. These patterns include issues such as missing information, inconsistencies, and ambiguous data entries.
While solutions exist for some patterns, as demonstrated in "A contextual approach to detecting synonymous and polluted activity labels in process event logs," a standardized, comprehensive approach to addressing these issues has been lacking. The PraeclarusPDQ framework, currently under development, aims to capture and standardize various solutions for these imperfection patterns and other process-data quality issues.
The Odigos framework, proposed by Emamjome et al. in "Alohomora: Unlocking data quality causes through event log context" and further explored in "Root-cause analysis of process-data quality problems," provides a structured approach for identifying the root causes of process-data quality problems. This framework forms the theoretical foundation for the current research.

## Related works

Recent research by [Suriadi Suriadi, Robert Andrews, Arthur H. M. ter Hofstede, and Moe Thandar Wynn. Event log imperfection patterns for process mining](SAtHW17) identified various event log imperfection patterns. While solutions exist for some patterns as demonstrated in [A contextual approach to detecting synonymous and polluted activity labels in process event logs](StHWS19), a standardized approach is lacking. The [PraeclarusPDQ framework](https://github.com/praeclaruspdq/PraeclarusPDQ/), currently under development, aims to capture various solutions for these imperfection patterns and other process-data quality issues.

## Methodology

This project falls under Project Type III: Process-Data Quality: Prevention and Mitigation. We will utilize the Odigos framework proposed by [Fahame Emamjome, Robert Andrews, Arthur H. M. ter Hofstede, and Hajo A. Reijers. "Alohomora: Unlocking data quality causes through event log context](EAtHR20) and further explored in [Root-cause analysis of process-data quality problems](AEtHR22). This framework provides a structured approach for identifying the root causes of process-data quality problems. We will develop a software plug-in for the PraeclarusPDQ framework specifically designed to analyze root causes of chosen event log imperfection patterns.

The methodology of this project falls under "Project Type III: Process-Data Quality: Prevention and Mitigation." It employs a multi-faceted approach:

a) Utilization of the Odigos Framework:

Systematic identification of root causes for data quality issues in event logs.
Application of semiotics to examine the creation, interpretation, and meaning of event data.

b) Development of PraeclarusPDQ Plug-in:

Creation of a model reader for extracting keywords from event logs.
Implementation of an NLP model for analyzing keywords and generating relevant questions.
Development of a root cause analysis plugin for interactive Q&A and identification of root causes.
Creation of a mitigation suggestions plugin for generating recommendations based on identified root causes.

c) Questionnaire-Based Analysis:

Design of structured questionnaires based on the Odigos framework.
Focus on various process elements that may impact data quality.
Consultation with experts to ensure comprehensive and precise questions.

d) Integration of Detection and Prevention Strategies:

Combination of existing detection methods with new prevention and mitigation approaches.
Focus on addressing root causes rather than just symptoms of data quality issues.


Experiment Setup and Implementation:
The experimental setup involves several key components:

a) Event Log Generation and Selection:

Utilization of tools like PLG (Process Log Generator) to create simulated event logs with known imperfections.
Selection of real-world event logs containing various imperfection patterns.

b) Process Model Discovery:

Application of process mining tools like ProM to discover process models from the event logs.

c) Plug-in Testing:

The developed PraeclarusPDQ plug-in can be applied to both simulated and real-world event logs.
Testing the plug-in's ability to identify root causes associated with various imperfection patterns.

d) Questionnaire Implementation:

Deployment of the questionnaire-based system to gather insights from stakeholders and process experts.

e) Validation:

Comparison of plug-in results with manual analysis to assess accuracy and effectiveness.
Iterative refinement of the plug-in based on test results and expert feedback.
## Experiment Setup and Implementation

- We will utilize sample event logs containing various event log imperfection patterns
- The plug-in will be tested on its ability to identify root causes associated with these patterns within the event logs

## Results and Analysis

This project contributes to improving process-data quality for process mining by leveraging the Odigos framework to identify root causes and developing a PraeclarusPDQ plug-in for targeted analysis of event log imperfection patterns. The project findings will provide valuable insights for preventing and mitigating process-data quality issues, ultimately leading to more reliable process mining results.

a) Identification of Imperfection Patterns:

Detailed catalog of imperfection patterns found in the analyzed event logs.
Frequency and severity analysis of each pattern.

b) Root Cause Analysis:

In-depth analysis of the underlying causes for each identified imperfection pattern.
Mapping of root causes to specific process elements or organizational factors.

c) Mitigation Strategies:

Generation of targeted mitigation strategies based on identified root causes and stakeholder feedback.
Prioritization of strategies based on potential impact and feasibility of implementation.

d) Visualization of Results:

Creation of charts, graphs, and dashboards to represent data quality issues, root causes, and proposed solutions.
Development of process maps highlighting areas prone to data quality issues.

e) Performance Evaluation:

Assessment of the plug-in's accuracy in identifying root causes compared to manual analysis.
Evaluation of the effectiveness of proposed mitigation strategies through simulations or pilot implementations.

## Conclusion

By integrating the PraeclarusPDQ framework with the Odigos framework, the research provides a systematic approach to identifying and addressing root causes of data quality issues in event logs.

The development of an automated questionnaire system and the application of advanced analytical techniques, including NLP and interactive root cause analysis, represent notable advancements in the field. This approach is distinctly proactive, offering both detection and prevention strategies for data quality issues, which is a departure from traditional reactive methods.
The research underscores the importance of addressing data quality at its source, potentially leading to more reliable and insightful process mining results. This, in turn, can enhance decision-making processes and improve overall operational efficiency in organizations.
Future research directions are outlined, including the refinement of root cause identification models, exploration of emerging patterns of data imperfection, and expansion of the framework's applicability across various industries. The project sets the stage for continued advancements in process mining and data quality management, promising significant impacts on how organizations understand and optimize their business processes.
## Publications
[//]: # "Note: Uncomment each once you uploaded the files to the repository"

<!-- 1. [Semester 7 report](./) -->
<!-- 2. [Semester 7 slides](./) -->
<!-- 3. [Semester 8 report](./) -->
<!-- 4. [Semester 8 slides](./) -->
<!-- 5. Author 1, Author 2 and Author 3 "Research paper title" (2021). [PDF](./). -->
1. [Semester 7 report](https://drive.google.com/file/d/1kZQn7F9XBBUq6D0MsOg-i9yXMTNZzutr/view?usp=sharing)
2. [Semester 7 slides](https://drive.google.com/file/d/1U8VDfEbv2PaztlMW1Vl1rWmWKHsdNkMh/view?usp=sharing)
3. Suriadi Suriadi, Robert Andrews, Arthur H. M. ter Hofstede and Moe Thandar Wynn "Event log imperfection patterns for process mining: Towards a systematic approach to cleaning event logs. Information Systems, 64:132–150" (2017). [SAtHW17](https://drive.google.com/file/d/1QzvOtoso2kMy3a9pgV5bukuGY7JlP6OB/view?usp=sharing).

4. Sareh Sadeghianasl, Arthur H. M. ter Hofstede, Moe Thandar Wynn, and Suriadi Suriadi " A contextual approach to detecting synonymous and polluted activity labels in process event logs." (2019). [StHWS19](https://drive.google.com/file/d/18P85017_vKgDZkjwrmG35h5XPtW79RmI/view?usp=sharing).

5. Fahame Emamjome, Robert Andrews, Arthur H. M. ter Hofstede, and Hajo A. Reijers. "Alohomora: Unlocking data quality causes through event log context." (2020). [EAtHR20](https://drive.google.com/file/d/18Jnk5VWdSqP6WEvAhgC8GCFHs7hz8RVr/view?usp=sharing).

6. Robert Andrews, Fahame Emamjome, Arthur H.M. ter Hofstede, and Hajo A. Reijers. "Root-cause analysis of process-data quality problems. Journal of Business Analytics, 5(5):51–75" (2022). [AEtHR22](https://drive.google.com/file/d/1lDMO8tp47HinIfBtX-A-SCfBErkKJ8Qh/view?usp=sharing).

 
## Links

[//]: # ( NOTE: EDIT THIS LINKS WITH YOUR REPO DETAILS )

- [Project Repository](https://github.com/cepdnaclk/e18-4yp-Extending-and-Implementing-Process-Mining-Techniques-Prevention-and-Mitigation)
- [Project Page](https://cepdnaclk.github.io/e18-4yp-Extending-and-Implementing-Process-Mining-Techniques-Prevention-and-Mitigation)
- [Department of Computer Engineering](http://www.ce.pdn.ac.lk/)
- [University of Peradeniya](https://eng.pdn.ac.lk/)

[//]: # "Please refer this to learn more about Markdown syntax"
[//]: # "https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet"
