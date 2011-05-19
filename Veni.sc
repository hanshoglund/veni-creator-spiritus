/* 
    Grainer and spacial feedback player for 'Veni Creator Spiritus'
    Copyright (c) Hans Höglund 2011 
   
    Requirements:
        SuperCollider > 3.1
        Project class by Hans Höglund
        SC3-Plugins
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
  
  /* Setup */
  classvar <fileName     = "/Users/hans/Documents/Media/Projects/Veni/veniMono.wav";
  classvar <numParts     = 8;              
  classvar <outputOffset = 2;
  classvar <numSpeakers  = 6;
  classvar <speakerDistance = 2.3;


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
  
  var partGroup;                                   
  var outputGroup;
  
  var <parts;  
  var <field;
  
  /* GUI */
  var <partWindow;
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

    field = Bus.audio(server, numChannels: 4);
    
    // Setup server and parts

    server.waitForBoot {
        parts = List[];
        numParts.do { parts.add(VeniPart.new(this)) };
                           
        SynthDef.new(\test, { 
          var p = Impulse.ar(2);      
          Out.ar(field, BFEncode2.ar(p, MouseX.kr(-1,1), MouseY.kr(1,-1), 0, 0.7));
        }).add.send(server);

        SynthDef.new(\decoder, {          
          var w, x, y, z;                  
          var speakers;

          #w, x, y, z = In.ar(field, 4);   
          speakers = BFDecode1.ar1(w, x, y, z,
            (0..numSpeakers-1).collect(_*2+1).collect(_*pi/numSpeakers), 
            0pi,
            speakerDistance,
            speakerDistance!numSpeakers,
            0);
          Out.ar(outputOffset, speakers);          
        }).add.send(server);
        
        partGroup   = Group.head(server);
        outputGroup = Group.tail(server);

        SystemClock.sched(0.5, {
          Synth.new(\test, target:partGroup);
          Synth.new(\decoder, target: outputGroup);
        });
    };
    
    // Create GUI

    partWindow = VeniPartWindow.new;
    bufferWindow = VeniBufferWindow.new;
    
    
    // Create MIDI responder
    
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
          21, {partWindow.widgets[0].gain.value = val / 127.0},
          22, {partWindow.widgets[1].gain.value = val / 127.0},
          23, {partWindow.widgets[2].gain.value = val / 127.0},
          24, {partWindow.widgets[3].gain.value = val / 127.0},
          25, {partWindow.widgets[4].gain.value = val / 127.0},
          26, {partWindow.widgets[5].gain.value = val / 127.0},
          27, {partWindow.widgets[6].gain.value = val / 127.0},
          28, {partWindow.widgets[7].gain.value = val / 127.0},
          31, {partWindow.widgets[0].feedback.value = val / 127.0},
          32, {partWindow.widgets[1].feedback.value = val / 127.0},
          33, {partWindow.widgets[2].feedback.value = val / 127.0},
          34, {partWindow.widgets[3].feedback.value = val / 127.0},
          35, {partWindow.widgets[4].feedback.value = val / 127.0},
          36, {partWindow.widgets[5].feedback.value = val / 127.0},
          37, {partWindow.widgets[6].feedback.value = val / 127.0},
          38, {partWindow.widgets[7].feedback.value = val / 127.0}
        );        
      });
    };
    
    partWindow.show;
    bufferWindow.show;
  }
}

VeniPart {
     
  /* Part-specific control buses */
  var <xBus;
  var <yBus;       
  var <gainBus;
  var <feedbackBus; 

  var player;  
  var filter;
  var panner;
  
  var <outputBus;
  
  *new { |veni|
    ^super.new.init(veni);
  }
  
  init { |veni|
    xBus        = Bus.control(veni.server);
    yBus        = Bus.control(veni.server);       
    gainBus     = Bus.control(veni.server);
    feedbackBus = Bus.control(veni.server); 

//    granulator = TGrains
//    filter = Klank
//    panner = PanB2

    outputBus   = Bus.audio(veni.server);
  }

}