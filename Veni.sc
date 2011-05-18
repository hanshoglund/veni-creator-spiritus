

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
      
/*      [src, chan, num, val].postln;*/
      
      AppClock.sched(0, {
        switch(num,     
          // dens, dur, rate
          41, {bufferWindow.dens.value = val / 127.0},
          42, {bufferWindow.dur.value = val / 127.0},
          43, {bufferWindow.rate.value = val / 127.0},
          
          // play button
          94, {bufferWindow.play.value = val / 127},
          
          // pos widgets
          21, {posWindow.widgets[0].gain.value = val / 127.0},
          22, {posWindow.widgets[1].gain.value = val / 127.0},
          23, {posWindow.widgets[2].gain.value = val / 127.0},
          24, {posWindow.widgets[3].gain.value = val / 127.0},
          25, {posWindow.widgets[4].gain.value = val / 127.0},
          26, {posWindow.widgets[5].gain.value = val / 127.0},
          27, {posWindow.widgets[6].gain.value = val / 127.0},
          28, {posWindow.widgets[7].gain.value = val / 127.0},
          31, {posWindow.widgets[0].feedback.value = val / 127.0},
          32, {posWindow.widgets[1].feedback.value = val / 127.0},
          33, {posWindow.widgets[2].feedback.value = val / 127.0},
          34, {posWindow.widgets[3].feedback.value = val / 127.0},
          35, {posWindow.widgets[4].feedback.value = val / 127.0},
          36, {posWindow.widgets[5].feedback.value = val / 127.0},
          37, {posWindow.widgets[6].feedback.value = val / 127.0},
          38, {posWindow.widgets[7].feedback.value = val / 127.0}
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