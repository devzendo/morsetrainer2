use warnings;
use strict;
my $track = 1;
my $mt2exe = "/Users/matt/Documents/Eclipse/morse-workspace/morsetrainer2/morsetrainer2-cmd-macosx/target/macosx/bin/mt2";

foreach my $w (12, 15, 20, 25) {
  foreach my $f (8, 12, 15, 20, 25) {
    if ($f <= $w) {
      foreach my $t ('-source letters', '-source numbers', '-source prosigns -source punctuation', '-source all') {
        print "w $w f $f $t\n";
        `$mt2exe -wpm $w -fwpm $f $t -length 5 -record "Track-$track.mp3" -contents BootCamp.txt`;
        $track = $track + 1;
      }
    }
  }
}

