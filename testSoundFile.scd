(
var window = Window.new("Grains",Rect(318, 719, 400, 150)).front;

var sfv    = SoundFileView.new(window,Rect(10, 10, 350, 80));
var rate   = NumberBox    .new(window,Rect(10, 95, 50, 20));
var pos    = NumberBox    .new(window,Rect(80, 95, 50, 20));
var dur    = NumberBox    .new(window,Rect(150, 95, 50, 20));
var play   = Button       .new(window,Rect(10, 120, 100, 20));
var sf     = SoundFile.new;                      

sf.openRead("/Users/hans/Documents/Media/Projects/Veni/veniMono.wav");
sfv.soundfile = sf;
sfv.gridOn = false;
sfv.readWithTask;
sfv.waveColors = Color.new(0.0,0.8,1.0)!2;
sfv.setSelectionColor(0, Color.new(0.2,0.6,0.8));
play.states = [["Play"], ["Stop"]];
~sfv = sfv;                                                           

)

// this is start, stop
~sfv.setSelection(0,[50000, 102000]).front
// this is start, length
~sfv.selections[0]




~buf = Buffer.read(s, path: "/Users/hans/Documents/Media/Projects/Veni/veniMono.wav", action: {});



{PlayBuf.ar(1, ~buf)!2}.play
(
{TGrains.ar(
	2, 
	bufnum: ~buf, 
	trigger: MouseButton.kr(lag:0),
	centerPos: MouseX.kr * 190,
	dur: 4,
	rate: MouseY.kr * 0.1 + 1 // must be subtle...
)}.play
)

Bus.audio(s)