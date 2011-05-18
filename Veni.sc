

Veni : Project {

  classvar fileName  = "/Users/hans/Documents/Media/Projects/Veni/veniMono.wav";
  classvar numParts  = 12;
  classvar numOutput = 8;

  var <>server;
  var <>file;
  var <>buffer;
  var <>midiResponder;
               
  var densBus;
  var durBus;
  var rateBus;
  var selectOffBus;     
  var selectLenBus;

  var field;
  var parts;
  
  // GUI
  var <posWindow;
  var <bufferWindow;
  
  init {      
    server = Server.default;

    file = SoundFile.new;
    file.openRead(fileName);                
    
    buffer = Buffer.read(server, path: fileName);

    densBus      = Bus.control(server);
    durBus       = Bus.control(server);
    rateBus      = Bus.control(server);
    selectOffBus = Bus.control(server);     
    selectLenBus = Bus.control(server);

    field = Bus.audio(server, 3);


    server.waitForBoot {
      // TODO create parts         
    
      // Create decoder       
//      var #[w, x, y] = In.ar(field);
//      {DecodeB2.ar(numOutput, w, x, y)}.play;
    };
    
    // Create GUI

    posWindow = VeniPosWindow.new.show;
    bufferWindow = VeniBufferWindow.new.show;
    
    
    // Create midi responder
    
    midiResponder = CCResponder.new { |src, chan, num, val| 
      [src, chan, num, val].postln;
      AppClock.sched(0, {
        switch(num, 
          41, {bufferWindow.dens.value = val / 127.0},
          42, {bufferWindow.dur.value = val / 127.0},
          43, {bufferWindow.rate.value = val / 127.0},
          94, {bufferWindow.play.value = val / 127}
        );        
      });
    };
  }
  
  
}

VeniPart {

  var thBus;
  var rhBus;       
  var gainBus;
  var feedbackBus; 

  var granulator;  
  var filter;
  var panner;
  
  var outputBus;
  
  *new {
    ^super.new.init;
  }
  
  init {
    thBus       = Bus.control(Veni.server);
    rhBus       = Bus.control(Veni.server);       
    gainBus     = Bus.control(Veni.server);
    feedbackBus = Bus.control(Veni.server); 

//    granulator = TGrains
//    filter = Klank
//    panner = PanB2

    outputBus   = Bus.audio(Veni.server);
  }

}