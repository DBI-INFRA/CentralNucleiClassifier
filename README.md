# CentralNucleiClassifier

### Background and Scope
This script was developed in the context of a core facility project to analyse nuclear positioning in muscle fibres. In healthy muscle fibers, the nuclei is positioned at the periphery of the cell; abnormal nuclear positioning, where the nuclei has moved to a more central location, is a common marker for myopathies. In this project, we identify muscle cells and nuclei from fluorescent images of muscle tissue sections, and then classify each cell based on the absence or presence of nuclei that have “detached“ from the periphery, in order to count the number of affected cells in the tissue.

See: [www.dbi-infra.eu/iacf-project-gallery/muscle-nuclei-classification](www.dbi-infra.eu/iacf-project-gallery/muscle-nuclei-classification)

### Software Requirements
QuPath (Tested on version 0.5.1)

### Usage
* Expected input image: Fluorescent images with at least two channels, one labelling the cell membrane with the channel name "Membrane" and the other labelling the nuclei, with the channel name "DAPI".

1. Download the repository
2. Create a Qupath Project folder and add the available classes, classifiers and scripts to the project folder.
3. Under `Automate > Project scripts...`, find and run MyonucleiDetectionClassfication. Parameters can be adjusted within the script to change the minimum allowed roi sizes and hole sizes for tissue segmentation, as well as for adjusting the watershed-based cell segmentation in the ImageJ macro.
