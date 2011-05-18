


(
{
        b = Buffer.read(s, path: "/Users/hans/Documents/Media/Test/Loops/cymbal.wav", action: {});
        p = PlayBuf.ar(1, b, loop: 1) * 0.5; // source
        
        #w, x, y, z = PanB.ar(p, LFSaw.kr(1/8), 0.6);
//        #w, x, y, z = BFEncode2.ar(p, MouseX.kr, MouseY.kr, 0, 0.9);
        
        #a, b, c, d, e, f = BFDecode1.ar1(w, x, y, z, 
          [1, 3, 5, 7, 9, 11].collect(_*pi/6), 
          0pi,
          3,
          3!6);

        
        Out.ar(2, [a, b, c, d, e, f]);




//       #a, b, c, d = Pan4.ar(p, MouseX.kr(-1,1), MouseY.kr(-1,1)* -1);
//       Out.ar(2, [a, b, b, c, d, a]);


}.play;
)


(
{
  Out.ar(2, Impulse.ar(1/4, (6..1).collect(_/6), 0.5))
}.play
)