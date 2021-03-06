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
  classvar <outputOffset = 17;
  classvar <numSpeakers  = 8;
  classvar <speakerDistance = 11;
  classvar <feedbackAmount = 0.6;

  var <server;
  var <file;
  var <buffer;
  var <midiResponder; 

  /* Global control buses */
  var <densBus;
  var <durBus;
  var <rateBus;
  var <selectOffBus;
  var <selectLenBus;

  var <partGroup;
  var <outputGroup;

  var <parts;
  var <field;
  var <mono;

  /* GUI */
  var <partWindow;
  var <bufferWindow;

  var playing = false;


  init {

    server = server ? Server.default;
    file   = SoundFile.new;

    file.openRead(fileName);

    server.waitForBoot {

        buffer = Buffer.read(server, path: fileName);

        densBus      = Bus.control(server);
        durBus       = Bus.control(server);
        rateBus      = Bus.control(server);
        selectOffBus = Bus.control(server);
        selectLenBus = Bus.control(server);

        field = Bus.audio(server, numChannels: 4);
        mono  = Bus.audio(server);

        parts = List[];
        numParts.do {
          parts.add(VeniPart.new(this))
        };

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

        // TODO should be sent lazily on the first play instead
        SystemClock.sched(0.5, {
          //Synth.new(\test, target:partGroup);
          Synth.new(\decoder, target: outputGroup);
        });
    };

    partWindow = VeniPartWindow.new(this);
    bufferWindow = VeniBufferWindow.new(this);


    midiResponder = CCResponder.new { |src, chan, num, val|

      switch(num,
        41, {densBus.value = val / 127.0},
        42, {durBus.value = val / 127.0},
        43, {rateBus.value = val / 127.0},

        94, {
          if(val != 0, { this.play }, { this.stop });
        },

        21, {parts[0].gainBus.value = val / 127.0},
        22, {parts[1].gainBus.value = val / 127.0},
        23, {parts[2].gainBus.value = val / 127.0},
        24, {parts[3].gainBus.value = val / 127.0},
        25, {parts[4].gainBus.value = val / 127.0},
        26, {parts[5].gainBus.value = val / 127.0},
        27, {parts[6].gainBus.value = val / 127.0},
        28, {parts[7].gainBus.value = val / 127.0},
        31, {parts[0].feedbackBus.value = val / 127.0},
        32, {parts[1].feedbackBus.value = val / 127.0},
        33, {parts[2].feedbackBus.value = val / 127.0},
        34, {parts[3].feedbackBus.value = val / 127.0},
        35, {parts[4].feedbackBus.value = val / 127.0},
        36, {parts[5].feedbackBus.value = val / 127.0},
        37, {parts[6].feedbackBus.value = val / 127.0},
        38, {parts[7].feedbackBus.value = val / 127.0}
      );

      AppClock.sched(0, {
        switch(num,
          41, {bufferWindow.dens.value = val / 127.0},
          42, {bufferWindow.dur.value = val / 127.0},
          43, {bufferWindow.rate.value = val / 127.0},

          94, {bufferWindow.play.value = val / 127},

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

  play {
    if (playing == false) {
      parts.collect(_.play)
    };
    playing = true;
  }

  stop {
    if (playing == true) {
      parts.collect(_.stop);
    };
    playing = false;
  }
}

VeniPart {

  var veni;

  /* Part-specific control buses */
  var <xBus;
  var <yBus;
  var <gainBus;
  var <feedbackBus;

  var <synthDef;
  var synth;

  *new { |v|
    ^super.new.init(v);
  }

  init { |v|
    veni        = v;
    xBus        = Bus.control(veni.server);
    yBus        = Bus.control(veni.server);
    gainBus     = Bus.control(veni.server);
    feedbackBus = Bus.control(veni.server);

    synthDef    = {
      var player = TGrains.ar(
        	2,
        	bufnum:    veni.buffer,
        	trigger:   LFNoise1.kr(4 * veni.densBus.kr + 0.04),
        	centerPos: WhiteNoise.kr * veni.selectLenBus.kr + veni.selectOffBus.kr,
        	dur:       (veni.durBus.kr * 13) + 0.08,
        	rate:      (veni.rateBus.kr * 0.2 - 0.1) + 1 // must be subtle...
        );                                  
      
      var feedback =
        DelayL.ar(InFeedback.ar(veni.mono), 0.2 + (0.3.rand), 0.5);

      var filteredFeedback = HPF.ar(LPF.ar(feedback, 500), 100);
          
      var signal = (player[0] * gainBus.kr) 
        + (filteredFeedback * feedbackBus.kr * Veni.feedbackAmount);
        
      var panner = BFEncode2.ar(
        signal,
        xBus.kr,
        yBus.kr,
        0,
        0.7
      );  
      Out.ar(veni.mono, signal);
      Out.ar(veni.field, panner)
    }.asSynthDef;

    synthDef.add.send(veni.server);
  }


  play {
    synth = Synth.new(synthDef.name, target: veni.partGroup);
  }

  stop {
    synth.free;
  }

}