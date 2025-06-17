/* Myonuclei Detection and Cell Classification
** TLYJ 20230905
*/

//Initialize script
import qupath.imagej.gui.ImageJMacroRunner

// Functional parameters
//// Tissue Segmentation
MinRegionSize = 100000.0   // Minimum region size
MinRegionHole = 10000.0    // Minimum size for closing hole in region
MinExcludeSize = 1000.0    // Minimum size of object for area to be excluded
MinExcludeHole = 1000.0    // Minimum size for closing hole in exclude area
//// Watershed Parameters
DownSamp = 2              // Image downsampling factor
GaussRad = 2              // Gaussian pre-filter radius (downsampled pix)  
Prominence = 1            // Object detection level (grayscale levels)
MinArea = 100             // Minimum object area (downsampled pix)


// Retrieve image data
imageData = getCurrentImageData()
imageName = getCurrentImageNameWithoutExtension()

// Remove all detection objects
clearDetections()

// If no Tissue annotation, create Tissue Annotation
annotations = getAnnotationObjects()
if (annotations.size()==0)
{   
    createAnnotationsFromPixelClassifier("Region", MinRegionSize, MinRegionHole)
    selectAnnotations()
    createAnnotationsFromPixelClassifier("Tissue", MinExcludeSize, MinExcludeHole)
    fireHierarchyUpdate()
}

// Add Nuclei as detection objects in entire region 
// (Done before cell detection to obtain nucleis not found inside cell)
selectObjects { p -> p.getPathClass() == getPathClass('Tissue') && p.isAnnotation() }
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImage":"DAPI","requestedPixelSizeMicrons":0.5,"backgroundRadiusMicrons":8.0,"backgroundByReconstruction":true,"medianRadiusMicrons":0.0,"sigmaMicrons":1.5,"minAreaMicrons":10.0,"maxAreaMicrons":400.0,"threshold":100.0,"watershedPostProcess":true,"cellExpansionMicrons":0.0,"includeNuclei":true,"smoothBoundaries":true,"makeMeasurements":false}')

// Instantiate IJ Macro Runner
params = new ImageJMacroRunner(getQuPath()).getParameterList()
params.getParameters().get('downsampleFactor').setValue(DownSamp)
params.getParameters().get('sendROI').setValue(true)
params.getParameters().get('sendOverlay').setValue(false)
params.getParameters().get('doParallel').setValue(false)
params.getParameters().get('clearObjects').setValue(false)
params.getParameters().get('getROI').setValue(false)
params.getParameters().get('getOverlay').setValue(true)
params.getParameters().getOverlayAs.setValue("Annotations")

// Define ImageJ Macro
macro = 'run("Select None");'+
'run("Options...", "iterations=1 count=1 black");'+
'run("Duplicate...", "duplicate channels=1");'+
'run("8-bit");'+
'run("Gaussian Blur...", "sigma='+GaussRad+'");'+
'run("Find Maxima...", "prominence='+Prominence+' light output=[Segmented Particles]");'+
'run("Restore Selection");'+
'run("Analyze Particles...", "size='+MinArea+'-Infinity show=Overlay exclude");'

// Run ImageJ Macro on Annotated area to detect "Cells"
tissues = getAnnotationObjects().findAll(){it.getPathClass() == getPathClass("Tissue") }
for (tissue in tissues) {
    ImageJMacroRunner.runMacro(params, imageData, null, tissue, macro)
}
cells = getAnnotationObjects().findAll(){it.getPathClass() == null}
for (cell in cells) {
    cell.setPathClass(getPathClass("Cells"))
}

// Classify Nuclei based on distance to cell boundary
detectionToAnnotationDistancesSigned(true)
runObjectClassifier("Detached");
