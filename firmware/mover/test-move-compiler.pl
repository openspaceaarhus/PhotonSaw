#!/usr/bin/perl
use strict;
use warnings;

my $F = 20000;     # 20 kHz step frequency;
my $A = 8*200/20000; # Accelerate to full speed in 1 second  
my $OS = 1<<30;

my @moves;
sub printMoves {
    print "bm";
    printf(" %x", scalar(@moves));

    for my $m (@moves) {
	printf(" %x", $m);
    }

    print "\n";
    @moves = ();
}

sub mout {
    my ($v) = @_;
    push @moves, $v;
}

sub encodeMove {
    my @u32;
    for my $v (@_) {
	push @u32, $v & 0xffffffff;
    }

    my ($ticks, 
	$xs, $ys, $zs, $as,
	$xa, $ya, $za, $aa) = @u32;
        
    my $head = 0x05aa0000;
    $head |= 1<<1 if $xs;
    $head |= 1<<2 if $ys;
    $head |= 1<<3 if $zs;
    $head |= 1<<4 if $as;

    $head |= 1<<5 if $xa;
    $head |= 1<<6 if $ya;
    $head |= 1<<7 if $za;
    $head |= 1<<8 if $aa;
    
    mout($head);
    mout($ticks);

    mout($xs) if $xs;
    mout($ys) if $ys;
    mout($zs) if $zs;
    mout($as) if $as;

    mout($xa) if $xa;
    mout($ya) if $ya;
    mout($za) if $za;
    mout($aa) if $aa;
}

# X delta steps, Y delta steps, start speed (steps / second), end speed
sub linexy {
    my ($xd, $yd, $s0, $s1) = @_;
    
    my $dist = sqrt($xd**2 + $yd**2);

    my $ticks;
    if (defined $s1) {
	die "Acceleration not implemented";
    } else {
	$ticks = int($F * $dist / $s0 + 0.5);
    }
    
    my $xs = int($OS*($xd / $ticks));
    my $ys = int($OS*($yd / $ticks));
    my $xa = 0;
    my $ya = 0;

#    print "\nxs=$xs ys=$ys dist=$dist ticks=$ticks\n"; 

    encodeMove($ticks,
	       $xs, $ys, 0, 0,
	       $xa, $ya, 0, 0);
}

linexy(1600, 0, 1600);
linexy(0, 1600, 1600);
linexy(-1600, -1600, 800);

printMoves();
