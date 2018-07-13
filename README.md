# NER_Final
NER testing on diffrent softwares

In order to run the code:
* Prerequisite: latest Java Version, NetBeans (8.2 version used)
* The code needs to be run using: `<-Dfile.encoding=UTF-8>` On NetBeans it can be set by changing the VMoptions found by doing the following: run - project configuration - customize - run - compile and copy paste the instruction in the VMoptions box that shows in the compile portion.
* Download the code from Github or Clone the repository on your computer.
* The main.Java class can be run directly with the present information. The test is run on an EU court case found in resources/input, in English with the Type "rule". Three types exist for Spanish: Nicknames, rule and other. 
In order to run the individual components such as CoreNLP in the NER folder, the main method needs to be changed similarly to the previous point.
* The output should resemble the ones seen on Fig.19 and Fig. 20 (can be found in the pdf "TFM-InesBadji"). 

* Note: The Libraries used are heavy it might thus take time to load them as well as to run the code. Up to 4 minutes depending on the text length on a Toshiba Portege Z30 core i7, 8.00GB.
