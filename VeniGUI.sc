VeniPartWindow {
  classvar <x = 4;
  classvar <y = 2;

  var veni;
  var window;
  var <widgets;

  *new { |v|
    ^super.new.init(v);
  }
  init { |v|
    veni = v;
  }

  show {
    window = Window.new("",Rect(500, 300, 890, 550)).front;
    widgets = List[];

    y.do { |y|
      x.do { |x|
        var w = VeniPartWidget.new(veni, window, [x, y]);
        widgets.add(w);
        w.draw;
      }
    };
  }

}


VeniPartWidget {
  var veni;
  var window;
  var <offset;
  var <id;

  var <pos;
  var <gain;
  var <feedback;

  *new { |veni, window, offset=#[0,0]|
    ^super.new.init(veni, window, offset);
  }

  init { |v, w, o|
    veni   = v;
    window = w;
    offset = o;
    id     = this.calculateId;
  }

  draw {
    pos      = Slider2D . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 15,  200, 200));
    gain     = Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 225, 200, 16));
    feedback = Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 245, 200, 16));
  
    pos.action = { |s|
      this.part.xBus.value = s.x * (-2) + 1;
      this.part.yBus.value = s.y * 2 - 1;
    };
    gain.action = { |s|
      this.part.gainBus.value = s.value;
    };
    feedback.action = { |s|
      this.part.feedbackBus.value = s.value;
    };
  }

  calculateId {
    ^offset[0] + (offset[1] * VeniPartWindow.x);
  }

  part {
    ^veni.parts[id];
  }
}



VeniBufferWindow {
  var veni;
  var window;

  var sf;
  var sfv;
  var <dens;
  var <dur;
  var <rate;
  var <play;

  *new { |v|
    ^super.new.init(v);
  }
  init { |v|
    veni = v;
  }


  show {

    window = Window.new("Grains", Rect(500, 130, 370, 150)).front;

    sf     = SoundFile.new;
    sfv    = SoundFileView . new(window, Rect(10, 10, 350, 80));
    dens   = NumberBox     . new(window, Rect(10, 95, 50, 20));
    dur    = NumberBox     . new(window, Rect(80, 95, 50, 20));
    rate   = NumberBox     . new(window, Rect(150, 95, 50, 20));
    play   = Button        . new(window, Rect(10, 120, 100, 20));

    sf.openRead(Veni.fileName);
    sfv.soundfile = sf;
    sfv.gridOn = false;
    sfv.readWithTask;
    sfv.waveColors = Color.new(0.0,0.8,1.0) ! 2;
    sfv.setSelectionColor(0, Color.new(0.2,0.6,0.8));

    play.states = [["Play"], ["Stop"]];

    sfv.action = {|c|
      var range = c.selections[0].collect(_/veni.file.sampleRate);
      veni.selectOffBus.value = range[0];
      veni.selectLenBus.value = range[1];
    };
    dens.action = {|c| 
      veni.densBus.value = c.value;
    };
    dur.action  = {|c| 
      veni.durBus.value = c.value;
    };
    rate.action = {|c| 
      veni.rateBus.value = c.value;
    };
    play.action = { |c|
      if(c.value != 0, { veni.play }, { veni.stop });
    };
  }
}