VeniPartWindow { 
  classvar <x = 4;
  classvar <y = 2;
  
  var window;
  var <widgets;
  
  show {
    window = Window.new("",Rect(500, 300, 890, 550)).front;
    widgets = List[];
    
    y.do { |y|
      x.do { |x|
        var w = VeniPartWidget.new(window, [x, y]);
        w.draw;
        widgets.add(w);
      }
    };      
  }
  
}

VeniPartWidget {
  var <window;
  var <offset;
  var <id; 
  var <part;
  
  var <pos;
  var <gain;
  var <feedback; 
  
  *new { |window, offset=#[0,0]|
    ^super.new.init(window, offset);
  }
  
  init { |w, o|
    window = w;
    offset = o;
    id     = this.calculateId;
  } 
  
  draw {
    pos      = Slider2D . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 15,  200, 200));
    gain     = Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 225, 200, 16));
    feedback = Slider   . new(window, Rect(offset[0] * 220 + 15, offset[1] * 260 + 245, 200, 16));
  } 
  
  calculateId {
    ^offset[0] + (offset[1] * VeniPartWindow.x);
  } 
}             


VeniBufferWindow {  
  
  var window;                
  
  var sf;
  var sfv;
  var <dens;
  var <dur;
  var <rate;
  var <play;

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
    
    // TODO sfv actions
    // TODO update actions
//    dens.action = {|c| c.value.postln};
//    dur.action  = {|c| c.value.postln};
//    rate.action = {|c| c.value.postln};
//    play.action = {|c| switch (c.value, 0, {}, 1, {})};
    
    play.states = [["Play"], ["Stop"]];
  }
}