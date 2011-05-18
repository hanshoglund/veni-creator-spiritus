/* 
    Grainer and spacial feedback player for 'Veni Creator Spiritus'
    Copyright (c) Hans Höglund 2011 
   
    Requirements:
        SuperCollider > 3.1
        Project class by Hans Höglund
        Cocoa, qt or swing GUI
        
    Usage:
        alt 1) Run SuperCollider application
               Choose Projects > Veni 
        alt 2) ./sclang
               Veni.initProject(Veni)
               
    Output:
        2D ambisonic through this.field
        Decoded ambisonic is written to (outputOffset..outputOffset+numOutput)
*/
Veni : Project {
  
  classvar <fileName     = "/Users/hans/Documents/Media/Projects/Veni/veniMono.wav";
  classvar <numParts     = 12;              
  classvar <outputOffset = 2;
  classvar <numOutput    = 8;

  var <server;
  var <file;
  var <buffer;
  var <midiResponder;
               
  /* Global control buses */
  var densBus;
  var durBus;
  var rateBus;
  var selectOffBus;     
  var selectLenBus;
  
  /* VeniPart instances to generate the sound */
  var parts;                                    
  
  /* Ambisonic sound field on the form [w, x, y] where w etc are buses*/
  var <field;
  
  /* GUI */
  var <posWindow;
  var <bufferWindow;


  init {      
    server = server ? Server.default;

    file = SoundFile.new;
    file.openRead(fileName);                
    
    buffer = Buffer.read(server, path: fileName);

    densBus      = Bus.control(server);
    durBus       = Bus.control(server);
    rateBus      = Bus.control(server);
    selectOffBus = Bus.control(server);     
    selectLenBus = Bus.control(server);

    field = Bus.audio(server, numChannels: 3);

    server.waitForBoot {
      {
        parts = [];
        numParts.do { parts.add(VeniPart.new(this)) };
      }.value;
        
      {
/*        var fieldIndices = (0..2).collect(_ + field.index);*/

        {
          Out.ar(16, PanB2.ar(Impulse.ar(4, 0, 0.8), MouseX.kr, MouseY.kr));
        }.play;
        
        {             
          Out.ar(2, DecodeB2.ar(4, In.ar(16), In.ar(17), In.ar(18)));
        }.play;
      }.value;



    };
    
    // Create GUI

    posWindow = VeniPosWindow.new.show;
    bufferWindow = VeniBufferWindow.new.show;
    
    
    // Create midi responder
    
    midiResponder = CCResponder.new { |src, chan, num, val| 
      
//      [src, chan, num, val].postln;

      // TODO update bus

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
  
  *new { |veni|
    ^super.new.init(veni);
  }
  
  init { |veni|
    thBus       = Bus.control(veni.server);
    rhBus       = Bus.control(veni.server);       
    gainBus     = Bus.control(veni.server);
    feedbackBus = Bus.control(veni.server); 

//    granulator = TGrains
//    filter = Klank
//    panner = PanB2

    outputBus   = Bus.audio(veni.server);
  }

}