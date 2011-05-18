VeniPosWindow { 
  var x = 4;
  var y = 3;
  
  var window;
  
  show {
    window = Window.new("",Rect(500, 84, 890, 790)).front;   
    y.do { |y|
      x.do { |x|
        this.addPosition([x, y]);
      }
    };
  }
  
  addPosition { | offset=#[0,0] |
    Slider2D . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 15,  200, 200));
    Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 225, 200, 16));
    Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 245, 200, 16));
  }
}              


VeniBufferWindow {  
  
  var window;                
  
  var sfv;
  var dens;
  var dur;
  var rate;
  var play;
  var sf;

  show {
    window = Window.new("Grains",Rect(100, 230, 370, 150)).front;

    sfv    = SoundFileView . new(window, Rect(10, 10, 350, 80));
    dens   = NumberBox     . new(window, Rect(80, 95, 50, 20));
    dur    = NumberBox     . new(window, Rect(150, 95, 50, 20));
    rate   = NumberBox     . new(window, Rect(10, 95, 50, 20));
    play   = Button        . new(window, Rect(10, 120, 100, 20));
    sf     = SoundFile.new;                      

    sf.openRead("/Users/hans/Documents/Media/Projects/Veni/veniMono.wav");
    sfv.soundfile = sf;
    sfv.gridOn = false;
    sfv.readWithTask;
    sfv.waveColors = Color.new(0.0,0.8,1.0)!2;
    sfv.setSelectionColor(0, Color.new(0.2,0.6,0.8));
    
    play.states = [["Play"], ["Stop"]];
  }
}