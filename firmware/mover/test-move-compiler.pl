#!/usr/bin/perl
use strict;
use warnings;
use Device::SerialPort;
#use Time::HiRes(

my $F = 50000;    # step frequency;
my $A = 1/$F;  # step/s^2 Accelerate to full speed in 1 second  
my $OS = 1<<30;

my @moves;
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

    $s0 /= $F;
    
    my $dist = sqrt($xd**2 + $yd**2);
    my $xv = $xd/$dist;
    my $yv = $yd/$dist;

    my $ticks;
    my $a = 0;
    if (defined $s1) {
	$s1 /= $F;
	# distance = (1/2)*acceleration*time^2
       	# d = s0*t+0.5*a*t^2 and a = (s1-s0)/t =>
	# t = 2*d/(s1+s0)  and a = (s1^2-s0^2)/(2*d) 

	$a = (($s1**2-$s0**2)/(2*$dist));	
	print "$s0 -> $s1 in $dist -> $a\n";
	if (abs($a) > $A) { # TODO: This doesn't work! Clamp at max acceleration
	    print "Clamped at $a --> $A\n";	    
	    $a = $a>0 ? $A : -$A;
	    $ticks = int(0.5+ ((sqrt($s0**2+2*$a*$dist)-$s0)/$a));
	} else {
	    $ticks = int(0.5+ 2*$dist/($s1+$s0));
	}

    } else {
	$ticks = int($dist / $s0 + 0.5);
	$a = 0;
    }

    my $xs = int($OS*$xv*$s0);
    my $ys = int($OS*$yv*$s0);
    my $xa = int($OS*$xv*$a);
    my $ya = int($OS*$yv*$a);

    print "  xs=$xs ys=$ys xa=$xa ya=$ya a=$a dist=$dist ticks=$ticks\n"; 

    encodeMove($ticks,
	       $xs, $ys, 0, 0,
	       $xa, $ya, 0, 0);
}

sub linexys {
    my ($xd, $yd, $s0) = @_;

    linexy(  $xd/10,   $yd/10, 0, $s0);
    linexy(8*$xd/10, 8*$yd/10, $s0);
    linexy(  $xd/10,   $yd/10, $s0, 0);
}

my $portName = "/dev/ttyACM0";
my $port = new Device::SerialPort($portName, 0) or die "Can't open $portName: $!\n";
$port->read_const_time(100);

sub portCmd {
    my ($cmd) = @_;
#    print STDERR "Running: $cmd ";
    
    $port->write("$cmd\n");

    my $patience = 10;
    my $res = '';
    while ($patience-- > 0) {
	my $st = $port->read(10000);
	$res .= $st;
	if ($res =~ /[\r\n]Ready[\r\n]/) {
	    $res =~ s/^\s+//;
	    $res =~ s/\s+$//;
#	    print STDERR " got result $res\n";
	    return $res;
	}
	print STDERR ".";
    }
    die "Failed to get response to command: $cmd\nResult: $res\n";
}

sub strMoves {
    my $res = "bm";
    $res .= sprintf(" %x", scalar(@moves));

    for my $m (@moves) {
	$res .= sprintf(" %x", $m);
    }

    @moves = ();
    return $res;
}

portCmd("ai 10c");
portCmd(sprintf("me %d %d %d", 0, 350, 3));
portCmd(sprintf("me %d %d %d", 1, 350, 3));
portCmd(sprintf("me %d %d %d", 2, 0, 3));
portCmd(sprintf("me %d %d %d", 3, 0, 3));


while (1) {

    for my $i (0..10) {
	linexys(1600, 1600, 5*1600);
	linexys(-1600, -1600, 5*1600);
    }

#    linexys(400, 400, 10*1600);
#    linexys(400, 1600, 10*1600);
#    linexys(-800, -2000, 10*1600);

    my $res = portCmd(strMoves());
    
    while (1) {
	if ($res =~ /buffer\.free\s+(\d+)\s+words/) {
	    my $moves = $1;
	    if ($moves > 4095-100) {
		last;
	    }
	    sleep(1);	    
	    $res = portCmd("bs");
	} else {
	    die "Unable to find buffer.free in $res\n";
	}
    }
}

    
my $res = '';
    while (1) {
	if ($res =~ /buffer\.inuse\s+(\d+)\s+words/) {
	    my $moves = $1;
	    if (!$moves) {
		last;
	    }
	    sleep(1);	    
	    $res = portCmd("bs");
	} else {
	    die "Unable to find buffer.inuse in $res\n";
	}
    }

    portCmd(sprintf("me %d %d %d", 0, 0, 3));
    portCmd(sprintf("me %d %d %d", 1, 0, 3));
    die "Done";
