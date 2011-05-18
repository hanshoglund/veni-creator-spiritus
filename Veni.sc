

Veni : Project {

  classvar fileName = "/Users/hans/Documents/Media/Projects/Veni/veniMono.wav";
  classvar numParts = 12;
  classvar numOutput = 8;

  var <>server;
  var <>file;
               
  var densBus;
  var durBus;
  var rateBus;
  var posBus;     
  
  var parts;
  var output;
  
  init {      
    file = SoundFile.new;
    file.openRead(fileName);
    VeniPosWindow.new.show;
    VeniBufferWindow.new.show;
  }
  
  
}

VeniPart {

  var xBus;
  var yBus;       
  var gainBus;
  var feedbackBus; 

  var granulator;  
  var filter;
  var panner;
  
  var outputBus;

}