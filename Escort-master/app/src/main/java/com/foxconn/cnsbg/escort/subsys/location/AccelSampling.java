/*
 * This file is based on Gabor Paller's work
 *
 * Copyright (C) 2011 Gabor Paller
 * http://mylifewithandroid.blogspot.com/2010/06/shake-walk-run.html
 * This code has been placed in the public domain.
 */

package com.foxconn.cnsbg.escort.subsys.location;

import android.util.Log;

import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;

import java.util.Date;

public class AccelSampling {

    class PeakWindow {
        int windowLength;
        int windowPtr;
        double window[];

        public PeakWindow( int windowLength ) {
            this.windowLength = windowLength;
            window = new double[ windowLength ];
            init();
        }

        public void init() {
            windowPtr = 0;
            for( int i = 0 ; i < windowLength ; ++i )
                window[ i ] = 0.0;
        }

        public void placeValue( double value ) {
            window[ windowPtr++ ] = value;
            windowPtr = adjustPtr( windowPtr );
        }

        public int adjustPtr( int ptr ) {
            if( ptr < 0 )
                return windowLength - 1;
            if( ptr >= windowLength )
                return 0;
            return ptr;
        }

        public int getWindowLength() {
            return windowLength;
        }

        public int getWindowPtr() {
            return windowPtr;
        }

        public double getValue( int ptr ) {
            return window[ ptr ];
        }
    }

    class AveragingWindow {
        int windowLength;
        int windowPtr;
        double window[];

        public AveragingWindow( int windowLength ) {
            this.windowLength = windowLength;
            window = new double[ windowLength ];
            init();
        }

        public void init() {
            windowPtr = 0;
            for( int i = 0 ; i < windowLength ; ++i )
                window[i] = 0.0;
        }

        public double calculatePower( double input ) {
            window[ windowPtr ] = input*input;
            windowPtr = ++windowPtr % windowLength;
            double result = 0.0;
            for( int i = 0 ; i < window.length ; ++i )
                result += window[i];
            result = Math.sqrt( result / (double)windowLength );
            return result;
        }
    }

    class DelayWindow {
        int windowLength;
        int windowPtr;
        double window[];

        public DelayWindow( int windowLength ) {
            this.windowLength = windowLength;
            window = new double[ windowLength ];
            init();
        }

        public void init() {
            windowPtr = 0;
            for( int i = 0 ; i < windowLength ; ++i )
                window[ i ] = 0.0;
        }

        public double placeValue( double input ) {
            double result = window[ windowPtr ];
            window[ windowPtr ] = input;
            windowPtr = ++windowPtr % windowLength;
            return result;
        }
    }

    class LevelKeeper {
        int keepLength;
        double levelKept;
        int levelKeepingCounter;

        public LevelKeeper( int keepLength ) {
            this.keepLength = keepLength;
            init();
        }

        public void init() {
            levelKeepingCounter = 0;
        }

        public double getLevel( double input ) {
            double result = input;
            --levelKeepingCounter;
            if( ( levelKeepingCounter < 0 ) || ( input > levelKept ) ) {
                levelKept = input;
                levelKeepingCounter = keepLength;
            } else {
                result = levelKept;
            }
            return result;
        }
    }

    public final boolean IDENTIFY_MOVEMENT_PATTERN = false;

    //=========================================================================
    private static final int SAMPLELIMIT = 8;
    private static final float MOVEMENT_THRESHOLD = SAMPLELIMIT * 0.15f;

    private float sampleWindow[] = new float[ SAMPLELIMIT ];
    private int currentSampleCounter = 0;
    private int samplesInWindow = 0;
    //=========================================================================

    private static final String LOG_TAG = AccelSampling.class.getSimpleName();
    static final int MAX_BUFFER_LEN = 40;

    private boolean currentlyShaking = false;
    private int shakePeakCtr = 0;
    private int shakeCtr = 0;

    private boolean currentlyWalking = false;
    private int stepCtr = 0;

    private boolean currentlyRunning = false;
    private int runStepCtr = 0;

    private boolean samplingStarted = false;
    private long sampleCounter;

    private double sampleBuffer[] = new double[MAX_BUFFER_LEN];
    private int sampleBufferPtr = 0;

    private AveragingWindow w3power = new AveragingWindow( 20 );
    private AveragingWindow w4power = new AveragingWindow( 20 );
    private AveragingWindow w5power = new AveragingWindow( 5 );

    private PeakWindow w3peakWindow = new PeakWindow( 10 );
    private PeakWindow w4peakWindow = new PeakWindow( 10 );
    private PeakWindow w5peakWindow = new PeakWindow( 5 );

    private DelayWindow w3delayWindow = new DelayWindow( 10 );
    private PeakWindow delayedw3peakWindow = new PeakWindow( 10 );

    private DelayWindow w4delayWindow = new DelayWindow( 10 );
    private PeakWindow delayedw4peakWindow = new PeakWindow( 5 );

    private DelayWindow w5delayWindow = new DelayWindow( 5 );
    private PeakWindow delayedw5peakWindow = new PeakWindow( 3 );

    private DelayWindow w5pwdelayWindow = new DelayWindow( 10 );
    private LevelKeeper w5pwdelayLevelKeeper = new LevelKeeper( 60 );

    private long w3peakPtr = -1L;
    private long w4peakPtr = -1L;
    private long w5peakPtr = -1L;

    private double w3[] = {
            -0.000489182807048,	// 1
            -0.000858652010111,	// 2
            -0.00113583821754,	// 3
            -0.00106498662267,	// 4
            -0.000358029941802,	// 5
            0.00116294815188,	// 6
            0.00338024348745,	// 7
            0.00573592771904,	// 8
            0.00724126323981,	// 9
            0.00672663269903,	// 10
            0.00332167463362,	// 11
            -0.00298492586564,	// 12
            -0.0109709108507,	// 13
            -0.0182613771714,	// 14
            -0.0219854832897,	// 15
            -0.0198603493183,	// 16
            -0.0112878078472,	// 17
            0.00205783949989,	// 18
            0.0165239993424,	// 19
            0.0276563694057,	// 20
            0.0318309886184,	// 21
            0.0276563694057,	// 22
            0.0165239993424,	// 23
            0.00205783949989,	// 24
            -0.0112878078472,	// 25
            -0.0198603493183,	// 26
            -0.0219854832897,	// 27
            -0.0182613771714,	// 28
            -0.0109709108507,	// 29
            -0.00298492586564,	// 30
            0.00332167463362,	// 31
            0.00672663269903,	// 32
            0.00724126323981,	// 33
            0.00573592771904,	// 34
            0.00338024348745,	// 35
            0.00116294815188,	// 36
            -0.000358029941802,	// 37
            -0.00106498662267,	// 38
            -0.00113583821754,	// 39
            -0.000858652010111	// 40
    };

    private double w4[] = {
            -0.000978365614096,	// 1
            -0.00227167643509,	// 2
            -0.000716059883604,	// 3
            0.0067604869749,	// 4
            0.0144825264796,	// 5
            0.00664334926724,	// 6
            -0.0219418217014,	// 7
            -0.0439709665794,	// 8
            -0.0225756156944,	// 9
            0.0330479986849,	// 10
            0.0636619772368,	// 11
            0.0330479986849,	// 12
            -0.0225756156944,	// 13
            -0.0439709665794,	// 14
            -0.0219418217014,	// 15
            0.00664334926724,	// 16
            0.0144825264796,	// 17
            0.0067604869749,	// 18
            -0.000716059883604,	// 19
            -0.00227167643509,	// 20
            -0.000978365614096	// 21
    };

    private double w5[] = {
            -0.00195673122819,	// 1
            -0.00143211976721,	// 2
            0.0289650529593,	// 3
            -0.0438836434028,	// 4
            -0.0451512313887,	// 5
            0.127323954474,	// 6
            -0.0451512313887,	// 7
            -0.0438836434028,	// 8
            0.0289650529593,	// 9
            -0.00143211976721	// 10
    };

    protected void startSampling() {
        if( samplingStarted )
            return;
        sampleCounter = 0L;
        for( int i = 0 ; i < sampleBuffer.length ; ++i )
            sampleBuffer[i] = 0.0;
        sampleBufferPtr = 0;

        w3power.init();
        w4power.init();
        w5power.init();

        w3peakWindow.init();
        w4peakWindow.init();
        w5peakWindow.init();

        w3delayWindow.init();
        delayedw3peakWindow.init();

        w5delayWindow.init();
        delayedw5peakWindow.init();

        w5pwdelayWindow.init();
        w5pwdelayLevelKeeper.init();

        w3peakPtr = -1L;
        w4peakPtr = -1L;
        w5peakPtr = -1L;

        currentlyShaking = false;
        shakePeakCtr = 0;
        shakeCtr = 0;

        currentlyWalking = false;
        stepCtr = 0;

        currentlyRunning = false;
        runStepCtr = 0;

        samplingStarted = true;
    }

    // Processes one sample
    protected void processSample( float values[] ) {
        if( values.length < 3 )
            return;
        ++sampleCounter;
        if( sampleCounter > 0xFFFFFFF0L )
            sampleCounter = 0L;
        double ampl = Math.sqrt( (double)values[0]*values[0] +
                (double)values[1]*values[1] +
                (double)values[2]*values[2] );

        if (IDENTIFY_MOVEMENT_PATTERN) {
            ampl -= 9.81;	// subtract the Earth's gravity accel to decrease the initial ripple in the filters
            sampleBuffer[sampleBufferPtr++] = ampl;
            sampleBufferPtr = sampleBufferPtr % MAX_BUFFER_LEN;
            double w3out = convolution( w3 );
            double w4out = convolution( w4 );
            double w5out = convolution( w5 );
            w3peakWindow.placeValue( w3out );
            w4peakWindow.placeValue( w4out );
            w5peakWindow.placeValue( w5out );
            double w3pw = w3power.calculatePower( w3out );
            double w4pw = w4power.calculatePower( w4out );
            double w5pw = w5power.calculatePower( w5out );
            double delayedw5pw = w5pw;
            //double delayedw5pw = w5pwdelayWindow.placeValue( w5pw );
            double w5pwlevelkept = w5pwdelayLevelKeeper.getLevel( delayedw5pw );

            // Processes the shake movement
            processShake( w5out, w5pw );
            // Processes the walking movement
            processWalking( w3out, w3pw, w4pw, w5pwlevelkept );
            // Processes the running movement
            processRunning( w4out, w3pw, w4pw, w5pwlevelkept );
        }

        // movement itself is more important than movement pattern
        //=====================================================================
        putSampleIntoWindow( (float)ampl );
        if( sampleCounter > SAMPLELIMIT && detectMovement()) {
            long currentTime = new Date().getTime();
            CtrlCenter.setMotionDetectionTime(currentTime);
            //Log.d( LOG_TAG, "movement detected: " + currentTime);
        }
        //=====================================================================
    }

    //=========================================================================
    private void putSampleIntoWindow( float ampl ) {
        sampleWindow[ currentSampleCounter ] = ampl;
        currentSampleCounter = ( ++currentSampleCounter ) % SAMPLELIMIT;
        ++samplesInWindow;
        if( samplesInWindow > SAMPLELIMIT )
            samplesInWindow = SAMPLELIMIT;
    }

    private int stepBack( int currentPointer ) {
        int ptr = currentPointer - 1;
        if( ptr < 0 )
            ptr = SAMPLELIMIT - 1;
        return ptr;
    }

    // Returns true if movement was detected
    private boolean detectMovement() {
        int ptr = currentSampleCounter;
        float sum = 0.0f;
        for( int i = 0 ; i < samplesInWindow ; ++i ) {
            ptr = stepBack( ptr );
            sum += sampleWindow[ ptr ];
        }
        float avg = sum / (float)samplesInWindow;
        ptr = currentSampleCounter;
        float devSum = 0.0f;
        for( int i = 0 ; i < samplesInWindow ; ++i ) {
            ptr = stepBack( ptr );
            float diff = sampleWindow[ ptr ] - avg;
            if( diff < 0.0f )
                diff = -diff;
            devSum += diff;
        }
        return devSum >= MOVEMENT_THRESHOLD;
    }
    //=========================================================================

    private double convolution( double filter[] ) {
        int ctr = sampleBufferPtr - 1;
        double result = 0.0;
        for( int i = 0 ; i < filter.length ; ++i ) {
            if( ctr < 0 )
                ctr = MAX_BUFFER_LEN - 1;
            result += filter[i] * sampleBuffer[ctr];
            --ctr;
        }
        return result;
    }

    private long peakDetect( PeakWindow peakWindow ) {
        int peakOffset = 0;
        boolean peakSet = false;
        double peakValue = 0.0;
        int bufPtr = peakWindow.getWindowPtr();
        for( int i = -1 ; i >= -( peakWindow.getWindowLength() ) ; --i ) {
            bufPtr = peakWindow.adjustPtr( --bufPtr );
            if( !peakSet || ( peakWindow.getValue( bufPtr ) > peakValue ) ) {
                peakValue = peakWindow.getValue( bufPtr );
                peakSet = true;
                peakOffset = i;
            }
        }
        // don't accept peaks near to the beginning and end of buffer
        if( ( peakOffset <= -( peakWindow.getWindowLength() ) ) || ( peakOffset >= -1 ) )
            return -1L;	// no peak detected
        return sampleCounter+(long)peakOffset;
    }

    protected void walk( boolean isWalking, int count ) {
        Log.d(LOG_TAG, "walk: isWalking: " + isWalking + "; count: " + count);
    }

    public void run( boolean isRunning, int count ) {
        Log.d( LOG_TAG, "run: isRunning: "+isRunning+"; count: "+count );
    }

    public void shake( boolean isShaking, int count ) {
        Log.d( LOG_TAG, "shake: isShaking: "+isShaking+"; count: "+count );
    }

    private void processShake( double out_w5,double w5pw ) {
        boolean nowShaking = w5pw > 0.4;
        if( nowShaking != currentlyShaking ) {
            currentlyShaking = nowShaking;
            // Initialize counters if shaking has just been detected
            if( currentlyShaking ) {	// this means it was not shaking before
                shakePeakCtr = 0;
                shakeCtr = 0;
            }
            shake( currentlyShaking, shakeCtr );
        }
        double delayedout_w5 = w5delayWindow.placeValue( out_w5 );
        delayedw5peakWindow.placeValue( delayedout_w5 );

        // Run peak detection if shaking was detected
        if( currentlyShaking ) {
            long peakOffset = peakDetect( delayedw5peakWindow );
            if( peakOffset >= 0L ) {
                if( peakOffset != w5peakPtr ) {
                    Log.d( LOG_TAG, "processShake: new peak at "+peakOffset+" (previous at "+w5peakPtr+")" );
                    // New peak detected, step the shake counter (every second peak is a new shake)
                    w5peakPtr = peakOffset;
                    ++shakePeakCtr;
                    if( shakePeakCtr >= 2 ) {
                        shakePeakCtr = 0;
                        ++shakeCtr;
                        shake( currentlyShaking, shakeCtr );
                    }
                }
            }
        }
    }

    private void processWalking( double out_w3, double w3pw, double w4pw, double w5pw ) {
        boolean nowWalking = ( w5pw < 0.4 ) &&
                ( ( ( w3pw > 0.2 ) && ( w3pw < 0.8 ) ) ||
                        ( ( w4pw > 0.2 ) && ( w4pw < 0.4 ) ) );
        if( nowWalking != currentlyWalking ) {
            currentlyWalking = nowWalking;
            if( currentlyWalking ) {
                stepCtr = 0;
            }
            walk( currentlyWalking, stepCtr );
        }
        double delayedout_w3 = w3delayWindow.placeValue( out_w3 );
        delayedw3peakWindow.placeValue( delayedout_w3 );

        if( currentlyWalking ) {
            long peakOffset = peakDetect( delayedw3peakWindow );
            if( peakOffset >= 0L ) {
                if( peakOffset != w3peakPtr ) {
                    Log.d( LOG_TAG, "processRunning: new peak at "+peakOffset+" (previous at "+w3peakPtr+")" );
                    // New peak detected, step the step counter
                    w3peakPtr = peakOffset;
                    ++stepCtr;
                    walk( currentlyWalking, stepCtr );
                }
            }
        }
    }

    private void processRunning( double out_w4, double w3pw, double w4pw, double w5pw ) {
        boolean nowRunning = ( w5pw < 0.7 ) &&
                ( w4pw > 0.4 );
        if( nowRunning != currentlyRunning ) {
            currentlyRunning = nowRunning;
            if( currentlyRunning ) {
                runStepCtr = 0;
            }
            run( currentlyRunning, runStepCtr );
        }
        double delayedout_w4 = w4delayWindow.placeValue( out_w4 );
        delayedw4peakWindow.placeValue( delayedout_w4 );

        if( currentlyRunning ) {
            long peakOffset = peakDetect( delayedw4peakWindow );
            if( peakOffset >= 0L ) {
                if( peakOffset != w4peakPtr ) {
                    Log.d( LOG_TAG, "processRunning: new peak at "+peakOffset+" (previous at "+w4peakPtr+")" );
                    // New peak detected, step the step counter
                    w4peakPtr = peakOffset;
                    ++runStepCtr;
                    run( currentlyRunning, runStepCtr );
                }
            }
        }
    }
}
